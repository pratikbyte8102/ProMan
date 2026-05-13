package com.proman.issue.dto;

import com.proman.issue.IssueType;
import com.proman.issue.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record CreateIssueRequest(
    @NotNull IssueType type,
    @NotBlank @Size(max = 500) String title,
    String description,
    @NotNull Priority priority,
    UUID assigneeId,
    UUID sprintId,
    UUID parentId,
    Integer storyPoints,
    List<String> labels,
    Map<UUID, String> customFields
) {}
