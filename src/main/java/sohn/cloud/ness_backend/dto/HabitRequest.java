package sohn.cloud.ness_backend.dto;

public record HabitRequest(
    String name,
    String description,
    String frequency,
    Integer targetCount
) {}