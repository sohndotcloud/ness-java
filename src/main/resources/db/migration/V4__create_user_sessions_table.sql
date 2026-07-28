CREATE TABLE user_sessions (
                               id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                               user_id         UUID NOT NULL REFERENCES users(id),
                               token_hash      VARCHAR(255) NOT NULL UNIQUE,
                               issued_at       TIMESTAMP NOT NULL DEFAULT now(),
                               expires_at      TIMESTAMP NOT NULL,
                               revoked         BOOLEAN NOT NULL DEFAULT FALSE,
                               revoked_at      TIMESTAMP,
                               user_agent      VARCHAR(512),
                               ip_address      VARCHAR(45),
                               replaced_by     UUID REFERENCES user_sessions(id)
);

CREATE INDEX idx_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_sessions_token_hash ON user_sessions(token_hash);