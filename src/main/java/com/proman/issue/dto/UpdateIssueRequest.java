package com.proman.issue.dto;

import com.proman.issue.Priority;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public record UpdateIssueRequest(
    String title,
    String description,
    Priority priority,
    UUID assigneeId,
    UUID sprintId,
    Integer storyPoints,
    List<String> labels,
    Integer version,
    Map<UUID, String> customFields
) {}
