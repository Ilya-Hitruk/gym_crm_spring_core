CREATE TABLE IF NOT EXISTS trainees
(
    id            BIGINT NOT NULL,
    date_of_birth DATE,
    address       VARCHAR(255),
    CONSTRAINT pk_trainees PRIMARY KEY (id),
    CONSTRAINT fk_trainees_users FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);
