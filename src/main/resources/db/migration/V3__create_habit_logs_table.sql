CREATE TABLE habits (
                        id BIGSERIAL PRIMARY KEY,
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        frequency VARCHAR(64) NOT NULL DEFAULT 'daily',
                        target_count INT NOT NULL DEFAULT 1,
                        archived BOOLEAN NOT NULL DEFAULT false,
                        created_at TIMESTAMP NOT NULL DEFAULT now()
);