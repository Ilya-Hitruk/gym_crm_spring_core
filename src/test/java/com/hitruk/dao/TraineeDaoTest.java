package com.hitruk.dao;

import com.hitruk.gym.crm.model.dao.impl.TraineeDaoImpl;
import com.hitruk.gym.crm.model.entity.Trainee;
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
class TraineeDaoTest {
    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    @SuppressWarnings("rawtypes")
    private Query query;

    private TraineeDaoImpl dao;
    private Trainee trainee;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        when(sessionFactory.getCurrentSession()).thenReturn(session);
        dao = new TraineeDaoImpl(sessionFactory);

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
    void findAll_returnsAllTrainees() {
        when(session.createQuery(anyString(), eq(Trainee.class))).thenReturn(query);
        when(query.list()).thenReturn(List.of(trainee));

        List<Trainee> result = dao.findAll();

        assertEquals(1, result.size());
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
}
