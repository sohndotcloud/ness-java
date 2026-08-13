package sohn.cloud.ness_backend.dto;

import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.HabitLog;
import sohn.cloud.ness_backend.entity.SignalContact;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record HabitNotificationResponse(
    SignalContact contact
) {
    public static HabitNotificationResponse from(SignalContact signalContact) {
        return new HabitNotificationResponse(signalContact);
    }
}