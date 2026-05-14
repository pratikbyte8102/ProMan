package com.proman.comment.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record CreateCommentRequest(
    @NotBlank String body,
    UUID parentCommentId,
    List<UUID> mentions
) {}
