package com.hitruk.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.service.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private Dao<Long, Training> trainingDao;

    @Mock
    private Mapper<Training, TrainingDto> trainingMapper;

    @InjectMocks
    private TrainingServiceImpl service;

    private Training training;
    private TrainingDto trainingDto;

    @BeforeEach
    void setUp() {
        training = Training.builder()
                .id(1L)
                .traineeId(1L)
                .trainerId(1L)
                .name("Chest workout")
                .type(TrainingType.STRENGTH)
                .date(LocalDateTime.of(2026, Month.JUNE, 25, 10, 0))
                .duration(Duration.ofHours(1))
                .build();

        trainingDto = TrainingDto.builder()
                .id(1L)
                .traineeId(1L)
                .trainerId(1L)
                .name("Chest workout")
                .trainingType("STRENGTH")
                .date(LocalDateTime.of(2026, Month.JUNE, 25, 10, 0))
                .duration(Duration.ofHours(1))
                .build();
    }

    @Test
    void findById_existingId_returnsDto() {
        when(trainingDao.findById(1L)).thenReturn(Optional.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        TrainingDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Chest workout", result.getName());
        verify(trainingDao).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(trainingDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_validDto_returnsCreatedDto() {
        TrainingDto inputDto = TrainingDto.builder()
                .traineeId(2L)
                .trainerId(2L)
                .name("New Session")
                .trainingType("CARDIO")
                .date(LocalDateTime.now())
                .duration(Duration.ofMinutes(45))
                .build();
        Training inputEntity = Training.builder()
                .traineeId(2L)
                .trainerId(2L)
                .name("New Session")
                .type(TrainingType.CARDIO)
                .build();

        when(trainingMapper.toEntity(inputDto)).thenReturn(inputEntity);
        when(trainingDao.create(inputEntity)).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        TrainingDto result = service.create(inputDto);

        assertNotNull(result);
        verify(trainingDao).create(inputEntity);
    }

    @Test
    void findAll_returnsAllTrainings() {
        when(trainingDao.findAll()).thenReturn(List.of(training));
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        List<TrainingDto> result = service.findAll();

        assertEquals(1, result.size());
        verify(trainingDao).findAll();
    }
}
