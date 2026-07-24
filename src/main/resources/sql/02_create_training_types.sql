CREATE TABLE IF NOT EXISTS training_types
(
    id                 BIGSERIAL    NOT NULL,
    training_type_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_training_types PRIMARY KEY (id),
    CONSTRAINT uq_training_types_name UNIQUE (training_type_name)
);
