package com.proman.issue;

import com.proman.auth.User;
import com.proman.common.CursorPageResponse;
import com.proman.issue.dto.*;
import com.proman.workflow.StatusCategory;
import com.proman.workflow.dto.TransitionRequest;
import com.proman.workflow.dto.TransitionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class IssueController {

    private final IssueService issueService;

    @PostMapping("/api/projects/{projectId}/issues")
    public ResponseEntity<IssueResponse> create(
            @PathVariable UUID projectId,
            @Valid @RequestBody CreateIssueRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(issueService.create(projectId, request, user));
    }

    @GetMapping("/api/projects/{projectId}/issues")
    public ResponseEntity<CursorPageResponse<IssueResponse>> list(
            @PathVariable UUID projectId,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(required = false) UUID statusId,
            @RequestParam(required = false) UUID assigneeId,
            @RequestParam(required = false) UUID sprintId,
            @RequestParam(required = false) IssueType type,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) StatusCategory statusCategory) {
        return ResponseEntity.ok(issueService.listByProject(
            projectId, cursor, limit, statusId, assigneeId, sprintId, type, priority, statusCategory));
    }

    @GetMapping("/api/issues/{id}")
    public ResponseEntity<IssueResponse> get(@PathVariable UUID id) {
        return ResponseEntity.ok(issueService.getById(id));
    }

    @PatchMapping("/api/issues/{id}")
    public ResponseEntity<IssueResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateIssueRequest request) {
        return ResponseEntity.ok(issueService.update(id, request));
    }

    @DeleteMapping("/api/issues/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        issueService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/issues/{id}/transition")
    public ResponseEntity<IssueResponse> transition(
            @PathVariable UUID id,
            @Valid @RequestBody TransitionRequest request) {
        return ResponseEntity.ok(issueService.transition(id, request));
    }

    @GetMapping("/api/issues/{id}/transitions")
    public ResponseEntity<List<TransitionResponse.AllowedTransition>> allowedTransitions(
            @PathVariable UUID id) {
        return ResponseEntity.ok(issueService.getAllowedTransitions(id));
    }
}
