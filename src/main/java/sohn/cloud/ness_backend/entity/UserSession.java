package sohn.cloud.ness_backend.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_sessions")
public class UserSession {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "issued_at", nullable = false)
    private Instant issuedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "session_started_at", nullable = false)
    private Instant sessionStartedAt;

    @Column(nullable = false)
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "replaced_by")
    private UUID replacedBy;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    public String getTokenHash() {
        return tokenHash;
    }
    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }
    public Instant getIssuedAt() {
        return issuedAt;
    }
    public void setIssuedAt(Instant issuedAt) {
        this.issuedAt = issuedAt;
    }
    public Instant getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }
    public Instant getSessionStartedAt() {
        return sessionStartedAt;
    }
    public void setSessionStartedAt(Instant sessionStartedAt) {
        this.sessionStartedAt = sessionStartedAt;
    }
    public boolean isRevoked() {
        return revoked;
    }
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
    public Instant getRevokedAt() {
        return revokedAt;
    }
    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }
    public String getUserAgent() {
        return userAgent;
    }
    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public UUID getReplacedBy() {
        return replacedBy;
    }
    public void setReplacedBy(UUID replacedBy) {
        this.replacedBy = replacedBy;
    }
}