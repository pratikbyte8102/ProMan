package com.proman.activity.dto;

import java.time.Instant;
import java.util.UUID;

public record ActivityLogResponse(
    UUID id,
    UUID projectId,
    UUID issueId,
    UUID userId,
    String userName,
    String action,
    String fieldChanged,
    String oldValue,
    String newValue,
    Instant createdAt
) {}
