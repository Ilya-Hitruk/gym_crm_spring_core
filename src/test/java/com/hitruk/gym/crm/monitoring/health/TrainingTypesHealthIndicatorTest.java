package com.hitruk.gym.crm.monitoring.health;

import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypesHealthIndicatorTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypesHealthIndicator indicator;

    @Test
    void health_typesPresent_returnsUpWithCount() {
        when(trainingTypeRepository.findAll()).thenReturn(
                List.of(TrainingType.builder().id(1L).name("YOGA").build(),
                        TrainingType.builder().id(2L).name("CARDIO").build()));

        Health health = indicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals(2, health.getDetails().get("count"));
    }

    @Test
    void health_noTypes_returnsDown() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of());

        Health health = indicator.health();

        assertEquals(Status.DOWN, health.getStatus());
    }
}
