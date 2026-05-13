package com.proman.sprint.dto;

import java.time.LocalDate;

public record UpdateSprintRequest(
    String name,
    String goal,
    LocalDate startDate,
    LocalDate endDate
) {}
