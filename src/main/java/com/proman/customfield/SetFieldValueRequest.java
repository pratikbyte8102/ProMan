package com.proman.customfield;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record SetFieldValueRequest(
    @NotNull UUID fieldDefinitionId,
    String value
) {}
