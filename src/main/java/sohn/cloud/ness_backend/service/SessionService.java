package sohn.cloud.ness_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sohn.cloud.ness_backend.entity.UserSession;
import sohn.cloud.ness_backend.repo.UserSessionRepository;
import sohn.cloud.ness_backend.util.TokenHasher;

import java.beans.Transient;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
public class SessionService {

    private final UserSessionRepository sessionRepository;
    private static final SecureRandom secureRandom = new SecureRandom();

    public SessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public record RotatedSession(String rawRefreshToken, UUID userId) {}

    // Call this on login
    public String createSession(UUID userId, String userAgent, String ipAddress) {
        String rawToken = generateSecureToken();

        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setTokenHash(TokenHasher.hash(rawToken));
        session.setIssuedAt(Instant.now());
        session.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);

        sessionRepository.save(session);
        return rawToken; // send this to the client; never store it raw
    }

    // Call this when a refresh request comes in
    public RotatedSession rotateSession(String rawToken, String userAgent, String ipAddress) {
        String incomingHash = TokenHasher.hash(rawToken);
        UserSession existing = sessionRepository.findByTokenHash(incomingHash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (existing.isRevoked()) {
            // Token reuse detected — this is a strong signal of theft.
            // Revoke the entire session chain for this user as a precaution.
            revokeAllForUser(existing.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }

        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new SecurityException("Refresh token expired");
        }

        // Issue a new token, mark old one as replaced
        String newRawToken = createSession(existing.getUserId(), userAgent, ipAddress);
        String newHash = TokenHasher.hash(newRawToken);
        UserSession newSession = sessionRepository.findByTokenHash(newHash).orElseThrow();

        existing.setRevoked(true);
        existing.setRevokedAt(Instant.now());
        existing.setReplacedBy(newSession.getId());
        sessionRepository.save(existing);

        return new RotatedSession(newRawToken, existing.getUserId());
    }

    @Transactional
    public void revokeSession(String rawToken) {
        String hash = TokenHasher.hash(rawToken);
        sessionRepository.findByTokenHash(hash).ifPresent(s -> {
            s.setRevoked(true);
            s.setRevokedAt(Instant.now());
            sessionRepository.save(s);
        });
    }

    @Transactional
    public void revokeAllForUser(UUID userId) {
        sessionRepository.deleteByUserId(userId);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}