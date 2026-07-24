package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    Trainer save(Trainer trainer);

    Optional<Trainer> findByUsername(String username);

    Trainer update(Trainer trainer);

    boolean matchCredentials(String username, String password);

    void changePassword(String username, String newPassword);

    void setActive(String username, boolean isActive);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);

    List<Trainer> findAll();
}
