CREATE TABLE IF NOT EXISTS trainee_trainer
(
    trainee_id BIGINT NOT NULL,
    trainer_id BIGINT NOT NULL,
    CONSTRAINT pk_trainee_trainer PRIMARY KEY (trainee_id, trainer_id),
    CONSTRAINT fk_trainee_trainer_trainees FOREIGN KEY (trainee_id) REFERENCES trainees (id) ON DELETE CASCADE,
    CONSTRAINT fk_trainee_trainer_trainers FOREIGN KEY (trainer_id) REFERENCES trainers (id) ON DELETE CASCADE
);
