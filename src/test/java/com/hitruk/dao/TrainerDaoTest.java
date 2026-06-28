package com.hitruk.dao;

import com.hitruk.gym.crm.model.dao.TrainerDao;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import com.hitruk.gym.crm.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerDaoTest {

    @Mock
    private TrainerStorage trainerStorage;

    @InjectMocks
    private TrainerDao dao;

    private Trainer trainer;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();
    }

    @Test
    void findById_existing_returnsNonEmpty() {
        when(trainerStorage.findById(1L)).thenReturn(trainer);

        Optional<Trainer> result = dao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(trainerStorage).findById(1L);
    }

    @Test
    void findById_missing_returnsEmpty() {
        when(trainerStorage.findById(99L)).thenReturn(null);

        assertTrue(dao.findById(99L).isEmpty());
    }

    @Test
    void findAll_delegatesToStorage() {
        when(trainerStorage.findAll()).thenReturn(List.of(trainer));

        List<Trainer> result = dao.findAll();

        assertEquals(1, result.size());
        verify(trainerStorage).findAll();
    }

    @Test
    void create_delegatesToStorage() {
        when(trainerStorage.create(trainer)).thenReturn(trainer);

        Trainer result = dao.create(trainer);

        assertSame(trainer, result);
        verify(trainerStorage).create(trainer);
    }

    @Test
    void update_delegatesToStorageWithEntityId() {
        when(trainerStorage.update(1L, trainer)).thenReturn(trainer);

        Trainer result = dao.update(trainer);

        assertSame(trainer, result);
        verify(trainerStorage).update(1L, trainer);
    }

    @Test
    void delete_existing_returnsTrue() {
        when(trainerStorage.delete(1L)).thenReturn(true);

        assertTrue(dao.delete(1L));
    }

    @Test
    void delete_missing_returnsFalse() {
        when(trainerStorage.delete(99L)).thenReturn(false);

        assertFalse(dao.delete(99L));
    }
}
