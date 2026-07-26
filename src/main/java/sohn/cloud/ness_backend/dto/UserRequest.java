package sohn.cloud.ness_backend.dto;

public record UserRequest(
    String email,
    String timezone
) {}