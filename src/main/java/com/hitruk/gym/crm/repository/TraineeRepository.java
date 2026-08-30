package com.hitruk.gym.crm.repository;

import com.hitruk.gym.crm.entity.Trainee;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeRepository {
    Trainee save(Trainee trainee);

    Optional<Trainee> findByUsername(String username);

    Trainee update(Trainee trainee);

    void deleteByUsername(String username);

    void changePassword(String username, String newPassword);

    void setActive(String username, boolean isActive);

    List<Training> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                String trainerName, String trainingType);

    List<Trainer> getUnassignedTrainers(String traineeUsername);

    List<Trainer> updateTrainers(String traineeUsername, List<String> trainerUsernames);

    List<String> findUsernamesStartingWith(String prefix);

    boolean existsByFirstNameAndLastName(String firstName, String lastName);

    long countActive();
}
