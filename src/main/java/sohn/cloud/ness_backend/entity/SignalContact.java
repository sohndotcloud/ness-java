package sohn.cloud.ness_backend.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "signal_contacts")
public class SignalContact {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "signal_number", nullable = false)
    private String number;

    @Column(name = "display_name")
    private String name;

    @ManyToMany(mappedBy = "signalContacts")
    private Set<Habit> habits = new HashSet<>();

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Habit> getHabits() {
        return habits;
    }

    public void setHabits(Set<Habit> habits) {
        this.habits = habits;
    }
}