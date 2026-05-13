package com.proman.issue.dto;

import com.proman.auth.dto.UserResponse;
import com.proman.issue.IssueType;
import com.proman.issue.Priority;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record IssueResponse(
    UUID id,
    String issueKey,
    Integer issueNumber,
    UUID projectId,
    IssueType type,
    String title,
    String description,
    UUID statusId,
    String statusName,
    Priority priority,
    UserResponse assignee,
    UserResponse reporter,
    UUID sprintId,
    UUID parentId,
    Integer storyPoints,
    List<String> labels,
    Integer version,
    Instant createdAt,
    Instant updatedAt
) {}
