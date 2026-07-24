package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.model.entity.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    private final SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
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
