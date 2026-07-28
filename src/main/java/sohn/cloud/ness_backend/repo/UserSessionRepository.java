package sohn.cloud.ness_backend.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import sohn.cloud.ness_backend.entity.UserSession;

import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {
    Optional<UserSession> findByTokenHash(String tokenHash);
    void deleteByUserId(UUID userId);
}