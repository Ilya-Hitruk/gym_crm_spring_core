package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TrainingTypeDto;
import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeRepository trainingTypeRepository;

    @InjectMocks
    private TrainingTypeServiceImpl service;

    @Test
    void findAll_mapsEntitiesToDtos() {
        TrainingType yoga = TrainingType.builder().id(1L).name("YOGA").build();
        TrainingType cardio = TrainingType.builder().id(2L).name("CARDIO").build();
        when(trainingTypeRepository.findAll()).thenReturn(List.of(yoga, cardio));

        List<TrainingTypeDto> result = service.findAll();

        assertEquals(2, result.size());
        assertEquals(new TrainingTypeDto(1L, "YOGA"), result.get(0));
        assertEquals(new TrainingTypeDto(2L, "CARDIO"), result.get(1));
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        when(trainingTypeRepository.findAll()).thenReturn(List.of());

        assertTrue(service.findAll().isEmpty());
    }
}
