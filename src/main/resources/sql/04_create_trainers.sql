CREATE TABLE IF NOT EXISTS trainers
(
    id                BIGINT NOT NULL,
    specialization_id BIGINT NOT NULL,
    CONSTRAINT pk_trainers PRIMARY KEY (id),
    CONSTRAINT fk_trainers_users FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_trainers_training_types FOREIGN KEY (specialization_id) REFERENCES training_types (id)
);
