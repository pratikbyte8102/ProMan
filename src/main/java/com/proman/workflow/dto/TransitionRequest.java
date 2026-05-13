package com.proman.workflow.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TransitionRequest(
    @NotNull UUID toStatusId
) {}
