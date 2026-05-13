package com.proman.sprint;

import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.project.ProjectService;
import com.proman.sprint.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectService projectService;

    @Transactional
    public SprintResponse create(UUID projectId, CreateSprintRequest request) {
        var project = projectService.findProject(projectId);

        if (request.endDate().isBefore(request.startDate())) {
            throw new BusinessRuleException("End date must be after start date");
        }

        Sprint sprint = Sprint.builder()
            .project(project)
            .name(request.name())
            .goal(request.goal())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .status(SprintStatus.PLANNING)
            .build();
        return toResponse(sprintRepository.save(sprint));
    }

    public List<SprintResponse> listByProject(UUID projectId) {
        return sprintRepository.findByProjectIdOrderByStartDateDesc(projectId).stream()
            .map(this::toResponse)
            .toList();
    }

    public SprintResponse getById(UUID sprintId) {
        return toResponse(findSprint(sprintId));
    }

    @Transactional
    public SprintResponse update(UUID sprintId, UpdateSprintRequest request) {
        Sprint sprint = findSprint(sprintId);
        if (request.name() != null) sprint.setName(request.name());
        if (request.goal() != null) sprint.setGoal(request.goal());
        if (request.startDate() != null) sprint.setStartDate(request.startDate());
        if (request.endDate() != null) sprint.setEndDate(request.endDate());
        return toResponse(sprintRepository.save(sprint));
    }

    @Transactional
    public void delete(UUID sprintId) {
        if (!sprintRepository.existsById(sprintId)) {
            throw new ResourceNotFoundException("Sprint", sprintId);
        }
        sprintRepository.deleteById(sprintId);
    }

    public Sprint findSprint(UUID sprintId) {
        return sprintRepository.findById(sprintId)
            .orElseThrow(() -> new ResourceNotFoundException("Sprint", sprintId));
    }

    SprintResponse toResponse(Sprint s) {
        return new SprintResponse(
            s.getId(), s.getProject().getId(), s.getName(), s.getGoal(),
            s.getStartDate(), s.getEndDate(), s.getStatus(),
            s.getCreatedAt(), s.getUpdatedAt()
        );
    }
}
