package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TrainerService {
    TrainerDto create(TrainerDto dto);

    boolean matchCredentials(String username, String password);

    TrainerDto findByUsername(String username);

    void changePassword(String username, String oldPassword, String newPassword);

    TrainerDto update(TrainerDto dto);

    void setActive(String username, boolean isActive);

    List<TrainerDto> findAll();

    List<TraineeDto> getTrainees(String trainerUsername);

    List<TrainingDto> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName);
}