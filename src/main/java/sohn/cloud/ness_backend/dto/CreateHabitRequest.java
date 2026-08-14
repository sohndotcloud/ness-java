package sohn.cloud.ness_backend.dto;

import java.util.List;

public record CreateHabitRequest(String name, boolean notify2, List<String> signalContactNumbers) {
}