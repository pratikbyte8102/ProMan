package com.proman.workflow.dto;

import com.proman.workflow.StatusCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateStatusRequest(
    @NotBlank String name,
    @NotNull StatusCategory category
) {}
