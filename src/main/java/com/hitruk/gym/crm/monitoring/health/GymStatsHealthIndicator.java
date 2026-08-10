package com.hitruk.gym.crm.monitoring.health;

import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GymStatsHealthIndicator implements HealthIndicator {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;

    @Override
    public Health health() {
        return Health.up()
                .withDetail("activeTrainees", traineeRepository.countActive())
                .withDetail("activeTrainers", trainerRepository.countActive())
                .build();
    }
}
