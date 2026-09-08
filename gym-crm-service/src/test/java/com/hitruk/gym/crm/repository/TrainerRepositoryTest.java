package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.repository.impl.TrainerRepositoryImpl;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
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
    private EntityManager entityManager;
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
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        dao = new TrainerRepositoryImpl(entityManager);

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
    void findUsernamesStartingWith_returnsMatchingUsernames() {
        when(session.createQuery(anyString(), eq(String.class))).thenReturn(query);
        when(query.setParameter(eq("prefix"), any())).thenReturn(query);
        when(query.list()).thenReturn(List.of("Chris.Bumstead"));

        List<String> result = dao.findUsernamesStartingWith("Chris.Bumstead");

        assertEquals(List.of("Chris.Bumstead"), result);
        verify(query).setParameter("prefix", "Chris.Bumstead%");
    }

    @Test
    @SuppressWarnings("unchecked")
    void existsByFirstNameAndLastName_found_returnsTrue() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("firstName"), any())).thenReturn(query);
        when(query.setParameter(eq("lastName"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        assertTrue(dao.existsByFirstNameAndLastName("Chris", "Bumstead"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void existsByFirstNameAndLastName_notFound_returnsFalse() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("firstName"), any())).thenReturn(query);
        when(query.setParameter(eq("lastName"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(0L);

        assertFalse(dao.existsByFirstNameAndLastName("Nobody", "Nowhere"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void countActive_returnsCount() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.uniqueResult()).thenReturn(7L);

        assertEquals(7L, dao.countActive());
    }
}
