package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.entity.Trainee;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;
import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.repository.impl.TrainingRepositoryImpl;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryTest {

    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;

    private TrainingRepositoryImpl dao;
    private Training training;

    @BeforeEach
    void setUp() {
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        dao = new TrainingRepositoryImpl(entityManager);

        TrainingType type = TrainingType.builder().id(1L).name("FITNESS").build();
        Trainee trainee = Trainee.builder().id(1L).username("John.Smith").build();
        Trainer trainer = Trainer.builder().id(1L).username("Chris.Bumstead").specialization(type).build();

        training = Training.builder()
                .id(1L).trainee(trainee).trainer(trainer)
                .name("Morning Lift").type(type)
                .date(LocalDate.of(2024, Month.JANUARY, 15)).duration(60).build();
    }

    @Test
    void save_persistsEntity() {
        Training result = dao.save(training);

        verify(session).persist(training);
        assertSame(training, result);
    }
}

