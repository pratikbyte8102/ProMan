package com.proman.comment.dto;

import com.proman.auth.dto.UserResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CommentResponse(
    UUID id,
    UUID issueId,
    UserResponse author,
    UUID parentCommentId,
    String body,
    List<UUID> mentions,
    Instant createdAt,
    Instant updatedAt
) {}
