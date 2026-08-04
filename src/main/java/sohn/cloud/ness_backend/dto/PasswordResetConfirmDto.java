package sohn.cloud.ness_backend.dto;

public record PasswordResetConfirmDto(String token, String newPassword) {}