CREATE TABLE habit_logs (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            habit_id UUID NOT NULL REFERENCES habits(id),
                            log_date DATE NOT NULL,
                            completed_count INT NOT NULL DEFAULT 0,
                            logged_at TIMESTAMP NOT NULL DEFAULT now()
);