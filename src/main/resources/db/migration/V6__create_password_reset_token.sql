ALTER TABLE user_sessions
    ADD COLUMN session_started_at TIMESTAMPTZ;

UPDATE user_sessions
SET session_started_at = issued_at
WHERE session_started_at IS NULL;

ALTER TABLE user_sessions
    ALTER COLUMN session_started_at SET NOT NULL;