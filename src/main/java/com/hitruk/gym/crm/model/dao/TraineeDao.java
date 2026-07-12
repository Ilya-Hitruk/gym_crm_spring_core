package com.hitruk.gym.crm.model.dao;

import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeDao {
    Trainee save(Trainee trainee);

    Optional<Trainee> findByUsername(String username);

    Trainee update(Trainee trainee);

    void deleteByUsername(String username);

    boolean matchCredentials(String username, String password);

    void changePassword(String username, String newPassword);

    void setActive(String username, boolean isActive);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                String trainerName, String trainingType);

    List<Trainer> getUnassignedTrainers(String traineeUsername);

    List<Trainer> updateTrainers(String traineeUsername, List<String> trainerUsernames);

    List<Trainee> findAll();
}
