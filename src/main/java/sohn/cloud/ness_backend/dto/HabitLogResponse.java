package sohn.cloud.ness_backend.dto;

import sohn.cloud.ness_backend.entity.HabitLog;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HabitLogResponse(
    UUID id,
    LocalDate logDate,
    Instant loggedAt,
    Integer completedCount,
    String note
) {
    public static HabitLogResponse from(HabitLog log) {
        return new HabitLogResponse(
            log.getId(), log.getLogDate(), log.getLoggedAt(),
            log.getCompletedCount(), log.getNote()
        );
    }
}