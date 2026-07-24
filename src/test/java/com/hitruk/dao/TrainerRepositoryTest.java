package com.hitruk.dao;

import com.hitruk.gym.crm.repository.impl.TrainerRepositoryImpl;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainingType;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    @SuppressWarnings("rawtypes")
    private Query query;

    private TrainerRepositoryImpl dao;
    private Trainer trainer;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        dao = new TrainerRepositoryImpl(sessionFactory);

        TrainingType type = TrainingType.builder().id(1L).name("FITNESS").build();
        trainer = Trainer.builder()
                .id(1L).firstName("Chris").lastName("Bumstead")
                .username("Chris.Bumstead").password("pass").isActive(true)
                .specialization(type).build();
    }

    @Test
    void save_persistsEntity() {
        dao.save(trainer);
        verify(session).persist(trainer);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByUsername_found_returnsOptional() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainer));

        Optional<Trainer> result = dao.findByUsername("Chris.Bumstead");

        assertTrue(result.isPresent());
        assertEquals("Chris.Bumstead", result.get().getUsername());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByUsername_notFound_returnsEmpty() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Trainer> result = dao.findByUsername("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_mergesEntity() {
        when(session.merge(trainer)).thenReturn(trainer);

        Trainer result = dao.update(trainer);

        assertSame(trainer, result);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findAll_returnsAllTrainers() {
        when(session.createQuery(anyString(), eq(Trainer.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(trainer));

        List<Trainer> result = dao.findAll();

        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void matchCredentials_validCredentials_returnsTrue() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), any())).thenReturn(query);
        when(query.setParameter(eq("password"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        assertTrue(dao.matchCredentials("Chris.Bumstead", "pass"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void matchCredentials_invalidCredentials_returnsFalse() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), any())).thenReturn(query);
        when(query.setParameter(eq("password"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(0L);

        assertFalse(dao.matchCredentials("Chris.Bumstead", "wrong"));
    }
}
