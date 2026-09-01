package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);

    Optional<Trainer> findByUsername(String username);

    Trainer update(Trainer trainer);

    void changePassword(String username, String newPassword);

    void setActive(String username, boolean isActive);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);

    List<Trainer> findAll();

    List<String> findUsernamesStartingWith(String prefix);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    long countActive();
}
