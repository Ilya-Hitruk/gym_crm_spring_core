package com.hitruk.gym.crm.repository.impl;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;
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
public class TrainerRepositoryImpl implements TrainerRepository {
    private final SessionFactory sessionFactory;

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Trainer save(Trainer trainer) {
        session().persist(trainer);
        log.debug("Trainer persisted: username={}", trainer.getUsername());
        return trainer;
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return session()
                .createQuery("FROM Trainer t WHERE t.username = :username", Trainer.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer merged = session().merge(trainer);
        log.debug("Trainer updated: username={}", merged.getUsername());
        return merged;
    }

    @Override
    public boolean matchCredentials(String username, String password) {
        Long count = session()
                .createQuery(
                        "SELECT COUNT(t) FROM Trainer t WHERE t.username = :username AND t.password = :password",
                        Long.class)
                .setParameter("username", username)
                .setParameter("password", password)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        Trainer trainer = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        trainer.setPassword(newPassword);
        log.debug("Password changed for trainer: username={}", username);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        Trainer trainer = findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        trainer.setIsActive(isActive);
        log.debug("Trainer isActive={} for username={}", isActive, username);
    }

    @Override
    public List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        List<String> clauses = new ArrayList<>(List.of("t.trainer.username = :username"));
        Map<String, Object> params = new LinkedHashMap<>(Map.of("username", username));
        validateParams(fromDate, toDate, traineeName, clauses, params);

        return executeFilteredTrainingsQuery(clauses, params);
    }

    @Override
    public List<Trainer> findAll() {
        return session().createQuery("FROM Trainer", Trainer.class).list();
    }

    @Override
    public List<String> findUsernamesStartingWith(String prefix) {
        return session()
                .createQuery("SELECT t.username FROM Trainer t WHERE t.username LIKE :prefix ESCAPE '\\'", String.class)
                .setParameter("prefix", escapeLike(prefix) + "%")
                .list();
    }

    @Override
    public boolean existsByFirstNameAndLastName(String firstName, String lastName) {
        Long count = session()
                .createQuery(
                        "SELECT COUNT(t) FROM Trainer t WHERE LOWER(t.firstName) = LOWER(:firstName) " +
                                "AND LOWER(t.lastName) = LOWER(:lastName)",
                        Long.class)
                .setParameter("firstName", firstName)
                .setParameter("lastName", lastName)
                .uniqueResult();
        return count != null && count > 0;
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private List<Training> executeFilteredTrainingsQuery(List<String> clauses, Map<String, Object> params) {
        String hql = "FROM Training t WHERE " + String.join(" AND ", clauses);
        var query = session().createQuery(hql, Training.class);
        params.forEach(query::setParameter);
        return query.list();
    }

    private static void validateParams(LocalDate fromDate,
                                       LocalDate toDate,
                                       String traineeName,
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
        if (traineeName != null) {
            clauses.add("t.trainee.username = :traineeName");
            params.put("traineeName", traineeName);
        }
    }
}
