package com.hitruk.gym.crm.facade;

import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.service.TraineeService;
import com.hitruk.gym.crm.service.TrainerService;
import com.hitruk.gym.crm.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@Slf4j
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        log.info("GymFacade initialized");
    }


    public TrainerDto createTrainer(TrainerDto dto) {
        log.info("Facade: createTrainer");
        return trainerService.create(dto);
    }

    public boolean trainerMatchCredentials(String username, String password) {
        log.info("Facade: trainerMatchCredentials username={}", username);
        return trainerService.matchCredentials(username, password);
    }

    public TrainerDto getTrainerByUsername(String username) {
        log.info("Facade: getTrainerByUsername username={}", username);
        return trainerService.findByUsername(username);
    }

    public void changeTrainerPassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changeTrainerPassword username={}", username);
        trainerService.changePassword(username, oldPassword, newPassword);
    }

    public TrainerDto updateTrainer(TrainerDto dto) {
        log.info("Facade: updateTrainer username={}", dto.getUsername());
        return trainerService.update(dto);
    }

    public void setTrainerActive(String username, boolean isActive) {
        log.info("Facade: setTrainerActive username={}, isActive={}", username, isActive);
        trainerService.setActive(username, isActive);
    }

    public List<TrainerDto> getAllTrainers() {
        log.info("Facade: getAllTrainers");
        return trainerService.findAll();
    }

    public TraineeDto createTrainee(TraineeDto dto) {
        log.info("Facade: createTrainee");
        return traineeService.create(dto);
    }

    public boolean traineeMatchCredentials(String username, String password) {
        log.info("Facade: traineeMatchCredentials username={}", username);
        return traineeService.matchCredentials(username, password);
    }

    public TraineeDto getTraineeByUsername(String username) {
        log.info("Facade: getTraineeByUsername username={}", username);
        return traineeService.findByUsername(username);
    }

    public void changeTraineePassword(String username, String oldPassword, String newPassword) {
        log.info("Facade: changeTraineePassword username={}", username);
        traineeService.changePassword(username, oldPassword, newPassword);
    }

    public TraineeDto updateTrainee(TraineeDto dto) {
        log.info("Facade: updateTrainee username={}", dto.getUsername());
        return traineeService.update(dto);
    }

    public void setTraineeActive(String username, boolean isActive) {
        log.info("Facade: setTraineeActive username={}, isActive={}", username, isActive);
        traineeService.setActive(username, isActive);
    }

    public void deleteTrainee(String username) {
        log.info("Facade: deleteTrainee username={}", username);
        traineeService.deleteByUsername(username);
    }

    public List<TrainingDto> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                                 String trainerName, String trainingType) {
        log.info("Facade: getTraineeTrainings username={}", username);
        return traineeService.getTrainings(username, fromDate, toDate, trainerName, trainingType);
    }

    public List<TrainerDto> getUnassignedTrainers(String traineeUsername) {
        log.info("Facade: getUnassignedTrainers traineeUsername={}", traineeUsername);
        return traineeService.getUnassignedTrainers(traineeUsername);
    }

    public List<TrainerDto> updateTraineeTrainers(String traineeUsername, List<String> trainerUsernames) {
        log.info("Facade: updateTraineeTrainers traineeUsername={}", traineeUsername);
        return traineeService.updateTrainers(traineeUsername, trainerUsernames);
    }

    public TrainingDto createTraining(TrainingDto dto) {
        log.info("Facade: createTraining");
        return trainingService.create(dto);
    }
}
