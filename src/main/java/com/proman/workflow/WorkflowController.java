package com.proman.workflow;

import com.proman.workflow.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WorkflowController {

    private final WorkflowService workflowService;

    // --- Statuses ---

    @PostMapping("/api/projects/{projectId}/workflow/statuses")
    public ResponseEntity<WorkflowStatusResponse> createStatus(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateStatusRequest request) {
        WorkflowStatus status = workflowService.createStatus(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(status));
    }

    @GetMapping("/api/projects/{projectId}/workflow/statuses")
    public ResponseEntity<List<WorkflowStatusResponse>> listStatuses(@PathVariable UUID projectId) {
        List<WorkflowStatusResponse> statuses = workflowService.listStatuses(projectId).stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(statuses);
    }

    @DeleteMapping("/api/workflow/statuses/{id}")
    public ResponseEntity<Void> deleteStatus(@PathVariable UUID id) {
        workflowService.deleteStatus(id);
        return ResponseEntity.noContent().build();
    }

    // --- Transitions ---

    @PostMapping("/api/projects/{projectId}/workflow/transitions")
    public ResponseEntity<Void> createTransitionRule(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateTransitionRuleRequest request) {
        workflowService.createTransitionRule(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/projects/{projectId}/workflow/transitions")
    public ResponseEntity<List<TransitionResponse>> getTransitions(@PathVariable UUID projectId) {
        return ResponseEntity.ok(workflowService.getTransitions(projectId));
    }

    @DeleteMapping("/api/workflow/transitions/{id}")
    public ResponseEntity<Void> deleteTransitionRule(@PathVariable UUID id) {
        workflowService.deleteTransitionRule(id);
        return ResponseEntity.noContent().build();
    }

    private WorkflowStatusResponse toResponse(WorkflowStatus s) {
        return new WorkflowStatusResponse(s.getId(), s.getProject().getId(),
            s.getName(), s.getCategory(), s.getPosition(), s.getCreatedAt(), s.getUpdatedAt());
    }
}
