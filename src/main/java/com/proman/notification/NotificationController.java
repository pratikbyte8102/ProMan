package com.proman.notification;

import com.proman.auth.User;
import com.proman.notification.dto.NotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationRepository notificationRepository;

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<NotificationResponse>> list(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "false") boolean unreadOnly) {
        List<Notification> notifications = unreadOnly
            ? notificationRepository.findUnreadByUserId(user.getId())
            : notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return ResponseEntity.ok(notifications.stream().map(this::toResponse).toList());
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> unreadCount(@AuthenticationPrincipal User user) {
        long count = notificationRepository.countByUserIdAndReadFalse(user.getId());
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    @PostMapping("/{id}/read")
    @Transactional
    public ResponseEntity<Void> markAsRead(@PathVariable UUID id) {
        Notification n = notificationRepository.findById(id).orElse(null);
        if (n != null) {
            n.setRead(true);
            notificationRepository.save(n);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/read-all")
    @Transactional
    public ResponseEntity<Map<String, Integer>> markAllAsRead(@AuthenticationPrincipal User user) {
        int updated = notificationRepository.markAllAsRead(user.getId());
        return ResponseEntity.ok(Map.of("markedRead", updated));
    }

    private NotificationResponse toResponse(Notification n) {
        String issueKey = n.getIssue().getProject().getKey() + "-" + n.getIssue().getIssueNumber();
        return new NotificationResponse(
            n.getId(),
            n.getType(),
            n.getIssue().getId(),
            issueKey,
            n.getActor().getId(),
            n.getActor().getDisplayName(),
            n.getRead(),
            n.getCreatedAt()
        );
    }
}
