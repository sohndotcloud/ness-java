package sohn.cloud.ness_backend.repo;

import sohn.cloud.ness_backend.entity.SignalContact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SignalContactRepository extends JpaRepository<SignalContact, UUID> {
    Optional<SignalContact> findByNumber(String number);
}