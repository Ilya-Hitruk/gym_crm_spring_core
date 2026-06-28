package com.hitruk.dao;

import com.hitruk.gym.crm.model.dao.TraineeDao;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.storage.TraineeStorage;
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
class TraineeDaoTest {

    @Mock
    private TraineeStorage traineeStorage;

    @InjectMocks
    private TraineeDao dao;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder().id(1L).firstName("John").lastName("Smith").build();
    }

    @Test
    void findById_existing_returnsNonEmpty() {
        when(traineeStorage.findById(1L)).thenReturn(trainee);

        Optional<Trainee> result = dao.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(traineeStorage).findById(1L);
    }

    @Test
    void findById_missing_returnsEmpty() {
        when(traineeStorage.findById(99L)).thenReturn(null);

        Optional<Trainee> result = dao.findById(99L);

        assertTrue(result.isEmpty());
    }

    @Test
    void findAll_delegatesToStorage() {
        when(traineeStorage.findAll()).thenReturn(List.of(trainee));

        List<Trainee> result = dao.findAll();

        assertEquals(1, result.size());
        verify(traineeStorage).findAll();
    }

    @Test
    void create_delegatesToStorage() {
        when(traineeStorage.create(trainee)).thenReturn(trainee);

        Trainee result = dao.create(trainee);

        assertSame(trainee, result);
        verify(traineeStorage).create(trainee);
    }

    @Test
    void update_delegatesToStorageWithEntityId() {
        when(traineeStorage.update(1L, trainee)).thenReturn(trainee);

        Trainee result = dao.update(trainee);

        assertSame(trainee, result);
        verify(traineeStorage).update(1L, trainee);
    }

    @Test
    void delete_existing_returnsTrue() {
        when(traineeStorage.delete(1L)).thenReturn(true);

        assertTrue(dao.delete(1L));
        verify(traineeStorage).delete(1L);
    }

    @Test
    void delete_missing_returnsFalse() {
        when(traineeStorage.delete(99L)).thenReturn(false);

        assertFalse(dao.delete(99L));
    }
}
