package sohn.cloud.ness_backend.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import sohn.cloud.ness_backend.entity.PasswordResetToken;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, UUID> {
    Optional<PasswordResetToken> findByToken(String token);
}