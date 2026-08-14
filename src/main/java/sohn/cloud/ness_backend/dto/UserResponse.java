package sohn.cloud.ness_backend.dto;

import sohn.cloud.ness_backend.entity.User;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String email,
    String phoneNumber,
    String timezone,
    Instant createdAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getPhoneNumber(), u.getTimezone(), u.getCreatedAt());
    }
}