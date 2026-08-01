package sohn.cloud.ness_backend.dto;

import java.time.LocalDate;

public record HabitLogRequest(
    Integer count,
    LocalDate date
) {}
