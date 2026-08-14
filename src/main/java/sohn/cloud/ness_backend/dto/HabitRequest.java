package sohn.cloud.ness_backend.dto;

import java.util.List;

public record HabitRequest(
        String name,
        String description,
        String frequency,
        Integer targetCount,
        boolean notify2,
        List<String> signalContactNumbers
) {}