package com.hitruk.dao;

import com.hitruk.gym.crm.model.dao.TrainingDao;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingDaoTest {

    @Mock
    private TrainingStorage trainingStorage;

    @InjectMocks
    private TrainingDao dao;

    private Training training;

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
    }

    @Test
    void findById_existing_returnsNonEmpty() {
        when(trainingStorage.findById(1L)).thenReturn(training);

        Optional<Training> result = dao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals("Chest workout", result.get().getName());
        verify(trainingStorage).findById(1L);
    }

    @Test
    void findById_missing_returnsEmpty() {
        when(trainingStorage.findById(99L)).thenReturn(null);

        assertTrue(dao.findById(99L).isEmpty());
    }

    @Test
    void findAll_delegatesToStorage() {
        when(trainingStorage.findAll()).thenReturn(List.of(training));

        List<Training> result = dao.findAll();

        assertEquals(1, result.size());
        verify(trainingStorage).findAll();
    }

    @Test
    void create_delegatesToStorage() {
        when(trainingStorage.create(training)).thenReturn(training);

        Training result = dao.create(training);

        assertSame(training, result);
        verify(trainingStorage).create(training);
    }

    @Test
    void update_delegatesToStorageWithEntityId() {
        when(trainingStorage.update(1L, training)).thenReturn(training);

        Training result = dao.update(training);

        assertSame(training, result);
        verify(trainingStorage).update(1L, training);
    }

    @Test
    void delete_existing_returnsTrue() {
        when(trainingStorage.delete(1L)).thenReturn(true);

        assertTrue(dao.delete(1L));
    }

    @Test
    void delete_missing_returnsFalse() {
        when(trainingStorage.delete(99L)).thenReturn(false);

        assertFalse(dao.delete(99L));
    }
}
