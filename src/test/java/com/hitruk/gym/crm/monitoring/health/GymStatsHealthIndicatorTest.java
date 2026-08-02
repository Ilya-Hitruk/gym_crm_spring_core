package com.hitruk.gym.crm.monitoring.health;

import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymStatsHealthIndicatorTest {

    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private GymStatsHealthIndicator indicator;

    @Test
    void health_alwaysUp_withActiveCounts() {
        when(traineeRepository.countActive()).thenReturn(5L);
        when(trainerRepository.countActive()).thenReturn(3L);

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(5L, health.getDetails().get("activeTrainees"));
        assertEquals(3L, health.getDetails().get("activeTrainers"));
    }
}
