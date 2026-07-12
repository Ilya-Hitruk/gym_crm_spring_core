CREATE TABLE IF NOT EXISTS trainings
(
    id                BIGSERIAL    NOT NULL,
    trainee_id        BIGINT       NOT NULL,
    trainer_id        BIGINT       NOT NULL,
    training_name     VARCHAR(255) NOT NULL,
    training_type_id  BIGINT       NOT NULL,
    training_date     DATE         NOT NULL,
    training_duration INTEGER      NOT NULL,
    CONSTRAINT pk_trainings PRIMARY KEY (id),
    CONSTRAINT fk_trainings_trainees FOREIGN KEY (trainee_id) REFERENCES trainees (id),
    CONSTRAINT fk_trainings_trainers FOREIGN KEY (trainer_id) REFERENCES trainers (id),
    CONSTRAINT fk_trainings_training_types FOREIGN KEY (training_type_id) REFERENCES training_types (id)
);
