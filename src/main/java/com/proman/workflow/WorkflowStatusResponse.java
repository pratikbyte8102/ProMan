package com.proman.workflow;

import java.time.Instant;
import java.util.UUID;

public record WorkflowStatusResponse(
    UUID id,
    UUID projectId,
    String name,
    StatusCategory category,
    Integer position,
    Instant createdAt,
    Instant updatedAt
) {}
