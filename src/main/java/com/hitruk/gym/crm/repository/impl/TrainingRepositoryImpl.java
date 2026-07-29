package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.repository.TrainingRepository;
import com.hitruk.gym.crm.entity.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {
    private final SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Training save(Training training) {
        session().persist(training);
        log.debug("Training persisted: name={}", training.getName());
        return training;
    }
}
