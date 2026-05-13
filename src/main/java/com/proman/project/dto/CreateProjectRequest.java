package com.proman.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    @NotBlank @Size(max = 10) @Pattern(regexp = "^[A-Z][A-Z0-9]*$") String key,
    @NotBlank @Size(max = 255) String name,
    String description
) {}
