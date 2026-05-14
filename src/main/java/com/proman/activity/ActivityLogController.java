package com.proman.activity;

import com.proman.activity.dto.ActivityLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;

    @GetMapping("/api/issues/{issueId}/activity")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ActivityLogResponse>> getIssueActivity(@PathVariable UUID issueId) {
        List<ActivityLogResponse> activity = activityLogRepository
            .findByIssueIdOrderByCreatedAtDesc(issueId).stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(activity);
    }

    @GetMapping("/api/projects/{projectId}/activity")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ActivityLogResponse>> getProjectActivity(@PathVariable UUID projectId) {
        List<ActivityLogResponse> activity = activityLogRepository
            .findByProjectIdOrderByCreatedAtDesc(projectId).stream()
            .map(this::toResponse)
            .toList();
        return ResponseEntity.ok(activity);
    }

    private ActivityLogResponse toResponse(ActivityLog a) {
        return new ActivityLogResponse(
            a.getId(),
            a.getProject().getId(),
            a.getIssue() != null ? a.getIssue().getId() : null,
            a.getUser().getId(),
            a.getUser().getDisplayName(),
            a.getAction(),
            a.getFieldChanged(),
            a.getOldValue(),
            a.getNewValue(),
            a.getCreatedAt()
        );
    }
}
