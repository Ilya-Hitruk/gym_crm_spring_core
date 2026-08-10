package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.repository.impl.TraineeRepositoryImpl;
import com.hitruk.gym.crm.entity.Trainee;
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
class TraineeRepositoryTest {
    @Mock
    private EntityManager entityManager;
    @Mock
    private Session session;
    @Mock
    @SuppressWarnings("rawtypes")
    private Query query;

    private TraineeRepositoryImpl dao;
    private Trainee trainee;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(entityManager.unwrap(Session.class)).thenReturn(session);
        dao = new TraineeRepositoryImpl(entityManager);

        trainee = Trainee.builder()
                .id(1L).firstName("John").lastName("Smith")
                .username("John.Smith").password("pass").isActive(true).build();
    }

    @Test
    void save_persistsEntity() {
        dao.save(trainee);
        verify(session).persist(trainee);
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByUsername_found_returnsOptional() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.of(trainee));

        Optional<Trainee> result = dao.findByUsername("John.Smith");

        assertTrue(result.isPresent());
        assertEquals("John.Smith", result.get().getUsername());
    }

    @Test
    @SuppressWarnings("unchecked")
    void findByUsername_notFound_returnsEmpty() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.uniqueResultOptional()).thenReturn(Optional.empty());

        Optional<Trainee> result = dao.findByUsername("Unknown");

        assertTrue(result.isEmpty());
    }

    @Test
    void update_mergesEntity() {
        when(session.merge(trainee)).thenReturn(trainee);

        Trainee result = dao.update(trainee);

        assertSame(trainee, result);
        verify(session).merge(trainee);
    }

    @Test
    @SuppressWarnings("unchecked")
    void matchCredentials_validCredentials_returnsTrue() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), any())).thenReturn(query);
        when(query.setParameter(eq("password"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        assertTrue(dao.matchCredentials("John.Smith", "pass"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void matchCredentials_invalidCredentials_returnsFalse() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("username"), any())).thenReturn(query);
        when(query.setParameter(eq("password"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(0L);

        assertFalse(dao.matchCredentials("John.Smith", "wrong"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void findUsernamesStartingWith_returnsMatchingUsernames() {
        when(session.createQuery(anyString(), eq(String.class))).thenReturn(query);
        when(query.setParameter(eq("prefix"), any())).thenReturn(query);
        when(query.list()).thenReturn(List.of("John.Smith", "John.Smith1"));

        List<String> result = dao.findUsernamesStartingWith("John.Smith");

        assertEquals(List.of("John.Smith", "John.Smith1"), result);
        verify(query).setParameter("prefix", "John.Smith%");
    }

    @Test
    @SuppressWarnings("unchecked")
    void existsByFirstNameAndLastName_found_returnsTrue() {
        when(session.createQuery(anyString(), eq(Long.class))).thenReturn(query);
        when(query.setParameter(eq("firstName"), any())).thenReturn(query);
        when(query.setParameter(eq("lastName"), any())).thenReturn(query);
        when(query.uniqueResult()).thenReturn(1L);

        assertTrue(dao.existsByFirstNameAndLastName("John", "Smith"));
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
        when(query.uniqueResult()).thenReturn(4L);

        assertEquals(4L, dao.countActive());
    }
}
