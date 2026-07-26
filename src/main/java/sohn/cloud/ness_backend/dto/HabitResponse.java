package sohn.cloud.ness_backend.dto;

import sohn.cloud.ness_backend.entity.Habit;

import java.time.Instant;
import java.util.UUID;

public record HabitResponse(
    UUID id,
    String name,
    String description,
    String frequency,
    Integer targetCount,
    boolean archived,
    Instant createdAt
) {
    public static HabitResponse from(Habit h) {
        return new HabitResponse(
            h.getId(), h.getName(), h.getDescription(),
            h.getFrequency(), h.getTargetCount(), h.isArchived(), h.getCreatedAt()
        );
    }
}