package sohn.cloud.ness_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sohn.cloud.ness_backend.entity.Habit;
import sohn.cloud.ness_backend.entity.HabitLog;
import sohn.cloud.ness_backend.repo.HabitLogRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Service
public class HabitLogService {

    private final HabitLogRepository repo;

    public HabitLogService(HabitLogRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public HabitLog logCompletion(Habit habit, LocalDate date, ZoneId userZone, int count) {
        LocalDate today = Instant.now().atZone(userZone).toLocalDate();

        HabitLog log = repo.findByHabitIdAndLogDate(habit.getId(), date)
            .orElseGet(() -> {
                HabitLog newLog = new HabitLog();
                newLog.setHabit(habit);
                newLog.setLogDate(date);
                return newLog;
            });

        log.setCompletedCount(log.getCompletedCount() + count);
        log.setLoggedAt(Instant.now());
        return repo.save(log);
    }

    @Transactional
    public void deleteLog(UUID habitId, LocalDate logDate) {
        repo.deleteByHabitIdAndLogDate(habitId, logDate);
    }
}