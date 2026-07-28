package sohn.cloud.ness_backend.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import sohn.cloud.ness_backend.entity.UserSession;
import sohn.cloud.ness_backend.repo.UserSessionRepository;
import sohn.cloud.ness_backend.util.TokenHasher;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.UUID;

@Service
public class SessionService {

    private final UserSessionRepository sessionRepository;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Duration MAX_SESSION_AGE = Duration.ofHours(1);

    public SessionService(UserSessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public record RotatedSession(String rawRefreshToken, UUID userId) {}

    public String createSession(UUID userId, String userAgent, String ipAddress) {
        return createSession(userId, userAgent, ipAddress, Instant.now());
    }

    private String createSession(UUID userId, String userAgent, String ipAddress, Instant sessionStartedAt) {
        String rawToken = generateSecureToken();
        UserSession session = new UserSession();
        session.setUserId(userId);
        session.setTokenHash(TokenHasher.hash(rawToken));
        session.setIssuedAt(Instant.now());
        session.setExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        session.setSessionStartedAt(sessionStartedAt);
        session.setUserAgent(userAgent);
        session.setIpAddress(ipAddress);
        sessionRepository.save(session);
        return rawToken;
    }

    public RotatedSession rotateSession(String rawToken, String userAgent, String ipAddress) {
        String incomingHash = TokenHasher.hash(rawToken);
        UserSession existing = sessionRepository.findByTokenHash(incomingHash)
                .orElseThrow(() -> new SecurityException("Invalid refresh token"));

        if (existing.isRevoked()) {
            revokeAllForUser(existing.getUserId());
            throw new SecurityException("Refresh token reuse detected");
        }
        if (existing.getExpiresAt().isBefore(Instant.now())) {
            throw new SecurityException("Refresh token expired");
        }
        if (existing.getSessionStartedAt().plus(MAX_SESSION_AGE).isBefore(Instant.now())) {
            revokeAllForUser(existing.getUserId());
            throw new SecurityException("Session exceeded maximum age");
        }

        String newRawToken = createSession(existing.getUserId(), userAgent, ipAddress, existing.getSessionStartedAt());
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