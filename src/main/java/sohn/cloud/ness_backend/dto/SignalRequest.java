package sohn.cloud.ness_backend.dto;

import java.util.List;

public record SignalRequest(String message, String number, List<String> recipients) {}