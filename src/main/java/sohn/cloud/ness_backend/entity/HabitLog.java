package sohn.cloud.ness_backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(
    name = "habit_logs",
    uniqueConstraints = @UniqueConstraint(columnNames = {"habit_id", "log_date"})
)
public class HabitLog {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "habit_id", nullable = false)
    private Habit habit;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt = Instant.now();

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "completed_count", nullable = false)
    private Integer completedCount = 0;

    private String note;

}