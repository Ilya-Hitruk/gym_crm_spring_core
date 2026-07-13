package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class TraineeRepositoryImpl implements TraineeRepository {
    private final SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Trainee save(Trainee trainee) {
        session().persist(trainee);
        log.debug("Trainee persisted: username={}", trainee.getUsername());
        return trainee;
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return session()
                .createQuery("FROM Trainee t WHERE t.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee merged = session().merge(trainee);
        log.debug("Trainee updated: username={}", merged.getUsername());
        return merged;
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        session().remove(trainee);
        log.debug("Trainee deleted: username={}", username);
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        Long count = session()
                .createQuery(
                        "SELECT COUNT(t) FROM Trainee t WHERE t.username = :username AND t.password = :password",
                        Long.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        trainee.setPassword(newPassword);
        log.debug("Password changed for trainee: username={}", username);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        Trainee trainee = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + username));
        trainee.setIsActive(isActive);
        log.debug("Trainee isActive={} for username={}", isActive, username);
    }

    @Override
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                       String trainerName, String trainingType) {
        List<String> clauses = new ArrayList<>(List.of("t.trainee.username = :username"));
        Map<String, Object> params = new LinkedHashMap<>(Map.of("username", username));
        validateParams(fromDate, toDate, trainerName, trainingType, clauses, params);

        return executeFilteredTrainingsQuery(clauses, params);
    }

    @Override
    public List<Trainer> getUnassignedTrainers(String traineeUsername) {
        return session()
                .createQuery(
                        "SELECT t FROM Trainer t WHERE t.isActive = true " +
                                "AND t.id NOT IN (" +
                                "  SELECT tr.id FROM Trainee te JOIN te.trainers tr WHERE te.username = :username" +
                                ")",
                        Trainer.class)
                .setParameter("username", traineeUsername)
                .list();
    }

    @Override
    public List<Trainer> updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        Trainee trainee = findByUsername(traineeUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + traineeUsername));
        List<Trainer> trainers = trainerUsernames.stream()
                .map(username -> session()
                        .createQuery("FROM Trainer t WHERE t.username = :username", Trainer.class)
                        .setParameter("username", username)
                        .uniqueResult())
                .filter(Objects::nonNull)
                .toList();
        trainee.setTrainers(new ArrayList<>(trainers));
        log.debug("Trainers updated for trainee: username={}", traineeUsername);
        return trainers;
    }

    @Override
    public List<Trainee> findAll() {
        return session().createQuery("FROM Trainee", Trainee.class).list();
    }

    private List<Training> executeFilteredTrainingsQuery(List<String> clauses, Map<String, Object> params) {
        String hql = "FROM Training t WHERE " + String.join(" AND ", clauses);
        var query = session()
                .createQuery(hql, Training.class);
        params.forEach(query::setParameter);
        return query.list();
    }

    private static void validateParams(LocalDate fromDate,
                                       LocalDate toDate,
                                       String trainerName,
                                       String trainingType,
                                       List<String> clauses,
                                       Map<String, Object> params) {
        if (fromDate != null) {
            clauses.add("t.date >= :fromDate");
            params.put("fromDate", fromDate);
        }
        if (toDate != null) {
            clauses.add("t.date <= :toDate");
            params.put("toDate", toDate);
        }
        if (trainerName != null) {
            clauses.add("t.trainer.username = :trainerName");
            params.put("trainerName", trainerName);
        }
        if (trainingType != null) {
            clauses.add("t.type.name = :trainingType");
            params.put("trainingType", trainingType);
        }
    }
}
