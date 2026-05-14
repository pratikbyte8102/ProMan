package com.proman.notification.dto;

import com.proman.notification.NotificationType;
import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
    UUID id,
    NotificationType type,
    UUID issueId,
    String issueKey,
    UUID actorId,
    String actorName,
    boolean read,
    Instant createdAt
) {}
