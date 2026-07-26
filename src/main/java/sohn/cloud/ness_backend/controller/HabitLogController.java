package sohn.cloud.ness_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.dto.HabitLogRequest;
import sohn.cloud.ness_backend.dto.HabitLogResponse;
import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.HabitLog;
import sohn.cloud.ness_backend.repo.HabitLogRepository;
import sohn.cloud.ness_backend.repo.HabitRepository;
import sohn.cloud.ness_backend.security.UserPrincipal;
import sohn.cloud.ness_backend.service.HabitLogService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/habits/{habitId}/logs")
public class HabitLogController {

    private final HabitLogService habitLogService;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;

    public HabitLogController(
        HabitLogService habitLogService,
        HabitRepository habitRepository,
        HabitLogRepository habitLogRepository
    ) {
        this.habitLogService = habitLogService;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
    }

    @PostMapping
    public HabitLogResponse logCompletion(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID habitId,
        @RequestBody(required = false) HabitLogRequest request
    ) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        int count = (request != null && request.count() != null) ? request.count() : 1;
        ZoneId userZone = ZoneId.of(principal.getTimezone());

        HabitLog log = habitLogService.logCompletion(habit, userZone, count);
        return HabitLogResponse.from(log);
    }

    @GetMapping
    public List<HabitLogResponse> getLogs(
        @PathVariable UUID habitId,
        @RequestParam(defaultValue = "30") int days
    ) {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusDays(days);

        return habitLogRepository
            .findByHabitIdAndLogDateBetweenOrderByLogDateAsc(habitId, start, end)
            .stream()
            .map(HabitLogResponse::from)
            .toList();
    }
}