package com.proman.customfield;

import java.time.Instant;
import java.util.UUID;

public record CustomFieldValueResponse(
    UUID id,
    UUID issueId,
    UUID fieldDefinitionId,
    String fieldName,
    FieldType fieldType,
    String value,
    Instant createdAt,
    Instant updatedAt
) {}
