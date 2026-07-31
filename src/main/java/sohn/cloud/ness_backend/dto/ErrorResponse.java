package sohn.cloud.ness_backend.dto;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public record ErrorResponse(HttpStatus status, String message, LocalDateTime timestamp) {}