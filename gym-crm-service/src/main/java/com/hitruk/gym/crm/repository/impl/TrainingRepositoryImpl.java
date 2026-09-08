package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.repository.TrainingRepository;
import com.hitruk.gym.crm.entity.Training;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class TrainingRepositoryImpl implements TrainingRepository {
    @PersistenceContext
    private EntityManager entityManager;

    TrainingRepositoryImpl() {
    }

    public TrainingRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Session session() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public Training save(Training training) {
        session().persist(training);
        log.debug("Training persisted: name={}", training.getName());
        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(session().get(Training.class, id));
    }

    @Override
    public void delete(Training training) {
        session().remove(training);
        log.debug("Training deleted: id={}", training.getId());
    }
}
