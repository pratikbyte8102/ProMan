package com.proman.workflow.dto;

import java.util.List;
import java.util.UUID;

public record TransitionResponse(
    UUID fromStatusId,
    String fromStatusName,
    List<AllowedTransition> allowedTransitions
) {
    public record AllowedTransition(UUID toStatusId, String toStatusName) {}
}
