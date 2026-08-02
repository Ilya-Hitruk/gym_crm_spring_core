package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.entity.TrainingType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    @PersistenceContext
    private EntityManager entityManager;

    TrainingTypeRepositoryImpl() {
    }

    public TrainingTypeRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private Session session() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    public TrainingType save(TrainingType trainingType) {
        session().persist(trainingType);
        log.debug("TrainingType persisted: name={}", trainingType.getName());
        return trainingType;
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        return session()
                .createQuery("FROM TrainingType t WHERE t.name = :name", TrainingType.class)
                .setParameter("name", name)
                .uniqueResultOptional();
    }

    @Override
    public List<TrainingType> findAll() {
        return session().createQuery("FROM TrainingType", TrainingType.class).list();
    }
}
