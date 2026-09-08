package com.hitruk.gym.crm.monitoring.metrics;

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GymMetricsTest {

    private SimpleMeterRegistry registry;
    private GymMetrics gymMetrics;

    @BeforeEach
    void setUp() {
        registry = new SimpleMeterRegistry();
        gymMetrics = new GymMetrics(registry);
    }

    @Test
    void incrementTraineeRegistrations_incrementsCounter() {
        gymMetrics.incrementTraineeRegistrations();
        gymMetrics.incrementTraineeRegistrations();

        assertEquals(2.0, registry.get("gym.trainee.registrations").counter().count());
    }

    @Test
    void incrementTrainerRegistrations_incrementsCounter() {
        gymMetrics.incrementTrainerRegistrations();

        assertEquals(1.0, registry.get("gym.trainer.registrations").counter().count());
    }

    @Test
    void incrementTrainingCreated_incrementsCounter() {
        gymMetrics.incrementTrainingCreated();
        gymMetrics.incrementTrainingCreated();
        gymMetrics.incrementTrainingCreated();

        assertEquals(3.0, registry.get("gym.trainings.added").counter().count());
    }
}
