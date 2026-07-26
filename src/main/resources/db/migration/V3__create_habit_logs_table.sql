CREATE TABLE habit_logs (
                            id BIGSERIAL PRIMARY KEY,
                            habit_id BIGINT NOT NULL REFERENCES habits(id),
                            log_date DATE NOT NULL,
                            completed_count INT NOT NULL DEFAULT 0,
                            logged_at TIMESTAMP NOT NULL DEFAULT now()
);