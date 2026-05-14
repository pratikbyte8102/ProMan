package com.proman.sprint.dto;

import com.proman.sprint.SprintStatus;
import java.time.LocalDate;

public record UpdateSprintRequest(
    String name,
    String goal,
    LocalDate startDate,
    LocalDate endDate,
    SprintStatus status
) {}
