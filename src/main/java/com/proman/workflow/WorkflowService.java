package com.proman.workflow;

import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.project.Project;
import com.proman.project.ProjectService;
import com.proman.workflow.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowService {

    private final WorkflowStatusRepository statusRepository;
    private final WorkflowTransitionRepository transitionRepository;
    private final ProjectService projectService;

    @Transactional
    public WorkflowStatus createStatus(UUID projectId, CreateStatusRequest request) {
        Project project = projectService.findProject(projectId);
        int nextPosition = statusRepository.countByProjectId(projectId) + 1;

        WorkflowStatus status = WorkflowStatus.builder()
            .project(project)
            .name(request.name())
            .category(request.category())
            .position(nextPosition)
            .build();
        return statusRepository.save(status);
    }

    @Transactional(readOnly = true)
    public List<WorkflowStatus> listStatuses(UUID projectId) {
        return statusRepository.findByProjectIdOrderByPositionAsc(projectId);
    }

    @Transactional
    public void deleteStatus(UUID statusId) {
        if (!statusRepository.existsById(statusId)) {
            throw new ResourceNotFoundException("WorkflowStatus", statusId);
        }
        statusRepository.deleteById(statusId);
    }

    @Transactional
    public void seedDefaultStatuses(Project project) {
        WorkflowStatus todo = WorkflowStatus.builder()
            .project(project).name("To Do").category(StatusCategory.TODO).position(1).build();
        WorkflowStatus inProgress = WorkflowStatus.builder()
            .project(project).name("In Progress").category(StatusCategory.IN_PROGRESS).position(2).build();
        WorkflowStatus done = WorkflowStatus.builder()
            .project(project).name("Done").category(StatusCategory.DONE).position(3).build();

        statusRepository.saveAll(List.of(todo, inProgress, done));

        // Create default transitions: TODO→IN_PROGRESS, IN_PROGRESS→DONE, IN_PROGRESS→TODO, DONE→TODO
        transitionRepository.saveAll(List.of(
            WorkflowTransition.builder().project(project).fromStatus(todo).toStatus(inProgress).build(),
            WorkflowTransition.builder().project(project).fromStatus(inProgress).toStatus(done).build(),
            WorkflowTransition.builder().project(project).fromStatus(inProgress).toStatus(todo).build(),
            WorkflowTransition.builder().project(project).fromStatus(done).toStatus(todo).build()
        ));
    }

    // --- Transitions ---

    @Transactional
    public WorkflowTransition createTransitionRule(UUID projectId, CreateTransitionRuleRequest request) {
        Project project = projectService.findProject(projectId);
        WorkflowStatus from = statusRepository.findById(request.fromStatusId())
            .orElseThrow(() -> new ResourceNotFoundException("WorkflowStatus", request.fromStatusId()));
        WorkflowStatus to = statusRepository.findById(request.toStatusId())
            .orElseThrow(() -> new ResourceNotFoundException("WorkflowStatus", request.toStatusId()));

        if (transitionRepository.findByProjectIdAndFromStatusIdAndToStatusId(
                projectId, request.fromStatusId(), request.toStatusId()).isPresent()) {
            throw new BusinessRuleException("Transition rule already exists");
        }

        WorkflowTransition transition = WorkflowTransition.builder()
            .project(project)
            .fromStatus(from)
            .toStatus(to)
            .autoAssignRole(request.autoAssignRole())
            .requiredFields(request.requiredFields() != null
                ? request.requiredFields().toArray(new String[0]) : null)
            .build();
        return transitionRepository.save(transition);
    }

    @Transactional(readOnly = true)
    public List<TransitionResponse> getTransitions(UUID projectId) {
        List<WorkflowTransition> transitions = transitionRepository.findByProjectId(projectId);

        return transitions.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                t -> t.getFromStatus().getId()))
            .entrySet().stream()
            .map(entry -> {
                WorkflowTransition first = entry.getValue().get(0);
                List<TransitionResponse.AllowedTransition> allowed = entry.getValue().stream()
                    .map(t -> new TransitionResponse.AllowedTransition(
                        t.getToStatus().getId(), t.getToStatus().getName()))
                    .toList();
                return new TransitionResponse(
                    first.getFromStatus().getId(),
                    first.getFromStatus().getName(),
                    allowed);
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<TransitionResponse.AllowedTransition> getAllowedTransitions(UUID projectId, UUID fromStatusId) {
        return transitionRepository.findByProjectIdAndFromStatusId(projectId, fromStatusId).stream()
            .map(t -> new TransitionResponse.AllowedTransition(
                t.getToStatus().getId(), t.getToStatus().getName()))
            .toList();
    }

    @Transactional
    public void deleteTransitionRule(UUID transitionId) {
        if (!transitionRepository.existsById(transitionId)) {
            throw new ResourceNotFoundException("WorkflowTransition", transitionId);
        }
        transitionRepository.deleteById(transitionId);
    }
}
