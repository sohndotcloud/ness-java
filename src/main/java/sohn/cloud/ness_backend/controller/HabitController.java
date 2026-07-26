package sohn.cloud.ness_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import sohn.cloud.ness_backend.dto.HabitRequest;
import sohn.cloud.ness_backend.dto.HabitResponse;
import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.User;
import sohn.cloud.ness_backend.repo.HabitRepository;
import sohn.cloud.ness_backend.repo.UserRepository;
import sohn.cloud.ness_backend.security.UserPrincipal;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/habits")
public class HabitController {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitController(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<HabitResponse> listHabits(@AuthenticationPrincipal UserPrincipal principal) {
        return habitRepository.findByUserIdAndArchivedFalse(principal.getUserId())
            .stream()
            .map(HabitResponse::from)
            .toList();
    }

    @PostMapping
    public ResponseEntity<HabitResponse> createHabit(
        @AuthenticationPrincipal UserPrincipal principal,
        @RequestBody HabitRequest request
    ) {
        User user = userRepository.getReferenceById(principal.getUserId());

        Habit habit = new Habit();
        habit.setUser(user);
        habit.setName(request.name());
        habit.setDescription(request.description());
        habit.setFrequency(request.frequency() != null ? request.frequency() : "daily");
        habit.setTargetCount(request.targetCount() != null ? request.targetCount() : 1);

        Habit saved = habitRepository.save(habit);
        return ResponseEntity
            .created(URI.create("/api/habits/" + saved.getId()))
            .body(HabitResponse.from(saved));
    }

    @GetMapping("/{habitId}")
    public HabitResponse getHabit(@PathVariable UUID habitId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return HabitResponse.from(habit);
    }

    @PatchMapping("/{habitId}/archive")
    public ResponseEntity<Void> archiveHabit(@PathVariable UUID habitId) {
        Habit habit = habitRepository.findById(habitId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        habit.setArchived(true);
        habitRepository.save(habit);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{habitId}")
    public ResponseEntity<Void> deleteHabit(@PathVariable UUID habitId) {
        habitRepository.deleteById(habitId);
        return ResponseEntity.noContent().build();
    }
}