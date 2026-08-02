package com.hitruk.gym.crm.monitoring.health;

import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TrainingTypesHealthIndicator implements HealthIndicator {
    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        if (types.isEmpty()) {
            return Health.down()
                    .withDetail("reason", "No training types found in reference table")
                    .build();
        }
        return Health.up()
                .withDetail("count", types.size())
                .withDetail("names", types.stream().map(TrainingType::getName).toList())
                .build();
    }
}
