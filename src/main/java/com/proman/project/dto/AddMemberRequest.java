package com.proman.project.dto;

import com.proman.project.ProjectRole;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record AddMemberRequest(
    @NotNull UUID userId,
    @NotNull ProjectRole role
) {}
