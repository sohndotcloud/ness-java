package sohn.cloud.ness_backend.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.dto.ErrorResponse;
import sohn.cloud.ness_backend.dto.HabitLogRequest;
import sohn.cloud.ness_backend.dto.HabitLogResponse;
import sohn.cloud.ness_backend.dto.SignalRequest;
import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.HabitLog;
import sohn.cloud.ness_backend.exception.HabitLogRequestException;
import sohn.cloud.ness_backend.exception.RegistrationUserExistsException;
import sohn.cloud.ness_backend.repo.HabitLogRepository;
import sohn.cloud.ness_backend.repo.HabitRepository;
import sohn.cloud.ness_backend.security.UserPrincipal;
import sohn.cloud.ness_backend.service.HabitLogService;
import sohn.cloud.ness_backend.service.SignalService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/habits/{habitId}/logs")
public class HabitLogController {

    private final HabitLogService habitLogService;
    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final SignalService signalService;

    public HabitLogController(
        HabitLogService habitLogService,
        HabitRepository habitRepository,
        HabitLogRepository habitLogRepository,
        SignalService signalService
    ) {
        this.habitLogService = habitLogService;
        this.habitRepository = habitRepository;
        this.habitLogRepository = habitLogRepository;
        this.signalService = signalService;
    }

    @PostMapping
    public HabitLogResponse logCompletion(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable UUID habitId,
        @RequestBody(required = false) HabitLogRequest request
    ) throws HabitLogRequestException {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (request == null) {
            throw new HabitLogRequestException();
        }

        int count = (request != null && request.count() != null) ? request.count() : 1;
        ZoneId userZone = ZoneId.of(principal.getTimezone());

        HabitLog log = habitLogService.logCompletion(habit, request.date(), userZone, count);
        signalService.sendMessage("+16027562858", "Testing number 1");
        return HabitLogResponse.from(log);
    }

    @ExceptionHandler(HabitLogRequestException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationUserExists(RegistrationUserExistsException ex) {
        ErrorResponse body = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, "Missing habit log request", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
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

    @DeleteMapping("/{logDate}")
    public ResponseEntity<Void> deleteLog(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable UUID habitId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate logDate
    ) {
        habitLogService.deleteLog(habitId, logDate);
        return ResponseEntity.noContent().build();
    }
}