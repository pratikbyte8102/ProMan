package com.proman.project.dto;

import com.proman.auth.dto.UserResponse;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String key,
    String name,
    String description,
    UserResponse owner,
    Instant createdAt,
    Instant updatedAt
) {}
