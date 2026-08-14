CREATE TABLE signal_contacts (
                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                 signal_number VARCHAR(20) NOT NULL,
                                 display_name VARCHAR(255)
);

CREATE TABLE habit_signal_contacts (
                                       habit_id UUID NOT NULL REFERENCES habits(id) ON DELETE CASCADE,
                                       signal_contact_id UUID NOT NULL REFERENCES signal_contacts(id) ON DELETE CASCADE,
                                       PRIMARY KEY (habit_id, signal_contact_id)
);