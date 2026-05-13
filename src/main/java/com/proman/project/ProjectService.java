package com.proman.project;

import com.proman.auth.AuthService;
import com.proman.auth.User;
import com.proman.auth.UserRepository;
import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.project.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProjectResponse create(CreateProjectRequest request, User currentUser) {
        if (projectRepository.existsByKey(request.key())) {
            throw new BusinessRuleException("Project key already exists: " + request.key());
        }

        Project project = Project.builder()
            .key(request.key())
            .name(request.name())
            .description(request.description())
            .owner(currentUser)
            .build();
        project = projectRepository.save(project);

        ProjectMember ownerMember = ProjectMember.builder()
            .id(new ProjectMemberId(project.getId(), currentUser.getId()))
            .project(project)
            .user(currentUser)
            .role(ProjectRole.ADMIN)
            .build();
        memberRepository.save(ownerMember);

        return toResponse(project);
    }

    public List<ProjectResponse> listForUser(UUID userId) {
        return projectRepository.findAllByMemberId(userId).stream()
            .map(this::toResponse)
            .toList();
    }

    public ProjectResponse getById(UUID projectId) {
        return toResponse(findProject(projectId));
    }

    @Transactional
    public ProjectResponse update(UUID projectId, UpdateProjectRequest request) {
        Project project = findProject(projectId);
        if (request.name() != null) project.setName(request.name());
        if (request.description() != null) project.setDescription(request.description());
        return toResponse(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project", projectId);
        }
        projectRepository.deleteById(projectId);
    }

    @Transactional
    public void addMember(UUID projectId, AddMemberRequest request) {
        Project project = findProject(projectId);
        User user = userRepository.findById(request.userId())
            .orElseThrow(() -> new ResourceNotFoundException("User", request.userId()));

        if (memberRepository.existsByIdProjectIdAndIdUserId(projectId, request.userId())) {
            throw new BusinessRuleException("User is already a member of this project");
        }

        ProjectMember member = ProjectMember.builder()
            .id(new ProjectMemberId(projectId, request.userId()))
            .project(project)
            .user(user)
            .role(request.role())
            .build();
        memberRepository.save(member);
    }

    @Transactional
    public void removeMember(UUID projectId, UUID userId) {
        ProjectMemberId id = new ProjectMemberId(projectId, userId);
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("ProjectMember", id);
        }
        memberRepository.deleteById(id);
    }

    public Project findProject(UUID projectId) {
        return projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project", projectId));
    }

    private ProjectResponse toResponse(Project p) {
        return new ProjectResponse(
            p.getId(), p.getKey(), p.getName(), p.getDescription(),
            AuthService.toUserResponse(p.getOwner()),
            p.getCreatedAt(), p.getUpdatedAt()
        );
    }
}
