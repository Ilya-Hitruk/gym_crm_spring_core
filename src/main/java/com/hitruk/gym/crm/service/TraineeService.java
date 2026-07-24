package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {
    TraineeDto create(TraineeDto dto);

    boolean matchCredentials(String username, String password);

    TraineeDto findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    TraineeDto update(TraineeDto dto);

    void setActive(String username, boolean isActive);

    void deleteByUsername(String username);

    List<TrainingDto> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                   String trainerName, String trainingType);

    List<TrainerDto> getUnassignedTrainers(String traineeUsername);

    List<TrainerDto> updateTrainers(String traineeUsername, List<String> trainerUsernames);
}