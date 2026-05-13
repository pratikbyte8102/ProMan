package com.proman.auth.dto;

import com.proman.auth.Role;
import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String displayName,
    Role role,
    Instant createdAt
) {}
