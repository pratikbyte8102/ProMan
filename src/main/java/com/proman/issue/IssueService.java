package com.proman.issue;

import com.proman.auth.AuthService;
import com.proman.auth.User;
import com.proman.auth.UserRepository;
import com.proman.common.CursorPageResponse;
import com.proman.common.CursorUtil;
import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.issue.dto.*;
import com.proman.project.Project;
import com.proman.project.ProjectService;
import com.proman.sprint.Sprint;
import com.proman.sprint.SprintRepository;
import com.proman.workflow.WorkflowService;
import com.proman.workflow.WorkflowStatus;
import com.proman.workflow.WorkflowStatusRepository;
import com.proman.workflow.StatusCategory;
import com.proman.workflow.dto.TransitionRequest;
import com.proman.workflow.dto.TransitionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class IssueService {

    private final IssueRepository issueRepository;
    private final ProjectService projectService;
    private final UserRepository userRepository;
    private final WorkflowStatusRepository statusRepository;
    private final SprintRepository sprintRepository;
    private final WorkflowService workflowService;

    @Transactional
    public IssueResponse create(UUID projectId, CreateIssueRequest request, User currentUser) {
        Project project = projectService.findProject(projectId);

        WorkflowStatus defaultStatus = statusRepository
            .findFirstByProjectIdAndCategoryOrderByPositionAsc(projectId, StatusCategory.TODO)
            .orElseThrow(() -> new BusinessRuleException("No TODO status configured for this project"));

        if (request.parentId() != null) {
            validateParentChild(request.type(), request.parentId());
        }

        int nextNumber = issueRepository.findMaxIssueNumber(projectId) + 1;

        Issue issue = Issue.builder()
            .issueNumber(nextNumber)
            .project(project)
            .type(request.type())
            .title(request.title())
            .description(request.description())
            .status(defaultStatus)
            .priority(request.priority())
            .reporter(currentUser)
            .storyPoints(request.storyPoints())
            .labels(request.labels() != null ? request.labels().toArray(new String[0]) : null)
            .version(0)
            .build();

        if (request.assigneeId() != null) {
            issue.setAssignee(userRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.assigneeId())));
        }

        if (request.sprintId() != null) {
            issue.setSprint(sprintRepository.findById(request.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", request.sprintId())));
        }

        if (request.parentId() != null) {
            issue.setParent(issueRepository.findById(request.parentId())
                .orElseThrow(() -> new ResourceNotFoundException("Issue", request.parentId())));
        }

        issue = issueRepository.save(issue);
        return toResponse(issue);
    }

    @Transactional(readOnly = true)
    public CursorPageResponse<IssueResponse> listByProject(UUID projectId, String cursor, int limit,
            UUID statusId, UUID assigneeId, UUID sprintId, IssueType type, Priority priority,
            com.proman.workflow.StatusCategory statusCategory) {

        boolean hasFilters = statusId != null || assigneeId != null || sprintId != null
            || type != null || priority != null || statusCategory != null;

        List<Issue> issues;

        if (hasFilters) {
            // When filters are applied, use specification-based query with offset pagination
            var spec = IssueSpecification.withFilters(projectId, statusId, assigneeId,
                sprintId, type, priority, statusCategory);
            issues = issueRepository.findAll(spec, PageRequest.of(0, limit + 1)).getContent();
            issues = new ArrayList<>(issues);
        } else if (cursor != null) {
            var decoded = CursorUtil.decode(cursor);
            issues = issueRepository.findByProjectIdWithCursor(
                projectId, decoded.createdAt(), decoded.id(), PageRequest.of(0, limit + 1));
        } else {
            issues = issueRepository.findByProjectIdFirstPage(projectId, PageRequest.of(0, limit + 1));
        }

        boolean hasMore = issues.size() > limit;
        if (hasMore) issues = issues.subList(0, limit);

        String nextCursor = (!hasFilters && hasMore && !issues.isEmpty())
            ? CursorUtil.encode(issues.get(issues.size() - 1).getCreatedAt(), issues.get(issues.size() - 1).getId())
            : null;

        return new CursorPageResponse<>(
            issues.stream().map(this::toResponse).toList(),
            nextCursor,
            hasMore
        );
    }

    @Transactional(readOnly = true)
    public IssueResponse getById(UUID issueId) {
        return toResponse(findIssue(issueId));
    }

    @Transactional
    public IssueResponse update(UUID issueId, UpdateIssueRequest request) {
        Issue issue = findIssue(issueId);

        if (request.title() != null) issue.setTitle(request.title());
        if (request.description() != null) issue.setDescription(request.description());
        if (request.priority() != null) issue.setPriority(request.priority());
        if (request.storyPoints() != null) issue.setStoryPoints(request.storyPoints());
        if (request.labels() != null) issue.setLabels(request.labels().toArray(new String[0]));
        if (request.assigneeId() != null) {
            issue.setAssignee(userRepository.findById(request.assigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("User", request.assigneeId())));
        }
        if (request.sprintId() != null) {
            issue.setSprint(sprintRepository.findById(request.sprintId())
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", request.sprintId())));
        }

        return toResponse(issueRepository.save(issue));
    }

    @Transactional
    public IssueResponse removeFromSprint(UUID issueId) {
        Issue issue = findIssue(issueId);
        issue.setSprint(null);
        return toResponse(issueRepository.save(issue));
    }

    @Transactional
    public void delete(UUID issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new ResourceNotFoundException("Issue", issueId);
        }
        issueRepository.deleteById(issueId);
    }

    @Transactional
    public IssueResponse transition(UUID issueId, TransitionRequest request) {
        Issue issue = findIssue(issueId);
        UUID projectId = issue.getProject().getId();
        UUID currentStatusId = issue.getStatus().getId();

        List<TransitionResponse.AllowedTransition> allowed =
            workflowService.getAllowedTransitions(projectId, currentStatusId);

        boolean isAllowed = allowed.stream()
            .anyMatch(t -> t.toStatusId().equals(request.toStatusId()));

        if (!isAllowed) {
            throw new BusinessRuleException(
                "Transition from '" + issue.getStatus().getName() + "' to the requested status is not allowed");
        }

        WorkflowStatus newStatus = statusRepository.findById(request.toStatusId())
            .orElseThrow(() -> new ResourceNotFoundException("WorkflowStatus", request.toStatusId()));
        issue.setStatus(newStatus);

        return toResponse(issueRepository.save(issue));
    }

    @Transactional(readOnly = true)
    public List<TransitionResponse.AllowedTransition> getAllowedTransitions(UUID issueId) {
        Issue issue = findIssue(issueId);
        return workflowService.getAllowedTransitions(
            issue.getProject().getId(), issue.getStatus().getId());
    }

    public Issue findIssue(UUID issueId) {
        return issueRepository.findById(issueId)
            .orElseThrow(() -> new ResourceNotFoundException("Issue", issueId));
    }

    private void validateParentChild(IssueType childType, UUID parentId) {
        Issue parent = issueRepository.findById(parentId)
            .orElseThrow(() -> new ResourceNotFoundException("Issue", parentId));

        boolean valid = switch (parent.getType()) {
            case EPIC -> childType == IssueType.STORY || childType == IssueType.TASK;
            case STORY -> childType == IssueType.SUBTASK;
            default -> false;
        };

        if (!valid) {
            throw new BusinessRuleException(
                parent.getType() + " cannot contain " + childType);
        }
    }

    IssueResponse toResponse(Issue i) {
        return new IssueResponse(
            i.getId(),
            i.getProject().getKey() + "-" + i.getIssueNumber(),
            i.getIssueNumber(),
            i.getProject().getId(),
            i.getType(),
            i.getTitle(),
            i.getDescription(),
            i.getStatus().getId(),
            i.getStatus().getName(),
            i.getPriority(),
            i.getAssignee() != null ? AuthService.toUserResponse(i.getAssignee()) : null,
            AuthService.toUserResponse(i.getReporter()),
            i.getSprint() != null ? i.getSprint().getId() : null,
            i.getParent() != null ? i.getParent().getId() : null,
            i.getStoryPoints(),
            i.getLabels() != null ? List.of(i.getLabels()) : List.of(),
            i.getVersion(),
            i.getCreatedAt(),
            i.getUpdatedAt()
        );
    }
}
