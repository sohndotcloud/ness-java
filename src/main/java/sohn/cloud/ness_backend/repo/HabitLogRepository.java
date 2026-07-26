package sohn.cloud.ness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sohn.cloud.ness_backend.entity.HabitLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitLogRepository extends JpaRepository<HabitLog, UUID> {

    Optional<HabitLog> findByHabitIdAndLogDate(UUID habitId, LocalDate logDate);

    List<HabitLog> findByHabitIdAndLogDateBetweenOrderByLogDateAsc(
        UUID habitId, LocalDate start, LocalDate end
    );
}