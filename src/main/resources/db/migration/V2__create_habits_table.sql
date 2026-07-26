CREATE TABLE habits (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL REFERENCES users(id),
                        name VARCHAR(255) NOT NULL,
                        description TEXT,
                        frequency VARCHAR(64) NOT NULL DEFAULT 'daily',
                        target_count INT NOT NULL DEFAULT 1,
                        is_archived BOOLEAN NOT NULL DEFAULT false,
                        created_at TIMESTAMP NOT NULL DEFAULT now()
);