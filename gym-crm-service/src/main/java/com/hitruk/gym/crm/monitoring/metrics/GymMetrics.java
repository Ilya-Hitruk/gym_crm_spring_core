package com.hitruk.gym.crm.monitoring.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class GymMetrics {
    private final Counter traineeRegistrations;
    private final Counter trainerRegistrations;
    private final Counter trainingsCreated;

    public GymMetrics(MeterRegistry registry) {
        this.traineeRegistrations = Counter.builder("gym.trainee.registrations")
                .description("Number of trainee profiles registered")
                .register(registry);
        this.trainerRegistrations = Counter.builder("gym.trainer.registrations")
                .description("Number of trainer profiles registered")
                .register(registry);
        this.trainingsCreated = Counter.builder("gym.trainings.added")
                .description("Number of trainings added")
                .register(registry);
    }

    public void incrementTraineeRegistrations() {
        traineeRegistrations.increment();
    }

    public void incrementTrainerRegistrations() {
        trainerRegistrations.increment();
    }

    public void incrementTrainingCreated() {
        trainingsCreated.increment();
    }
}
