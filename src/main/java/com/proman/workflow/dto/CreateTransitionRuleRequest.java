package com.proman.workflow.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateTransitionRuleRequest(
    @NotNull UUID fromStatusId,
    @NotNull UUID toStatusId,
    String autoAssignRole,
    List<String> requiredFields
) {}
