package sohn.cloud.ness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sohn.cloud.ness_backend.entity.Habit;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HabitRepository extends JpaRepository<Habit, UUID> {

    List<Habit> findByUserIdAndArchivedFalse(UUID userId);

    List<Habit> findByUserId(UUID userId);

    Optional<Habit> findByIdAndUserId(UUID habitId, UUID userId);
}