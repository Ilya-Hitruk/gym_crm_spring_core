package com.hitruk.gym.crm.facade;

import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.service.TraineeService;
import com.hitruk.gym.crm.service.TrainerService;
import com.hitruk.gym.crm.service.TrainingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

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

    public TraineeDto createTrainee(TraineeDto dto) {
        log.info("Facade: createTrainee");
        return traineeService.create(dto);
    }

    public TraineeDto updateTrainee(TraineeDto dto) {
        log.info("Facade: updateTrainee id={}", dto.getId());
        return traineeService.update(dto);
    }

    public boolean deleteTrainee(Long id) {
        log.info("Facade: deleteTrainee id={}", id);
        return traineeService.delete(id);
    }

    public TraineeDto getTrainee(Long id) {
        log.info("Facade: getTrainee id={}", id);
        return traineeService.findById(id);
    }

    public List<TraineeDto> getAllTrainees() {
        log.info("Facade: getAllTrainees");
        return traineeService.findAll();
    }

    public TrainerDto createTrainer(TrainerDto dto) {
        log.info("Facade: createTrainer");
        return trainerService.create(dto);
    }

    public TrainerDto updateTrainer(TrainerDto dto) {
        log.info("Facade: updateTrainer id={}", dto.getId());
        return trainerService.update(dto);
    }

    public TrainerDto getTrainer(Long id) {
        log.info("Facade: getTrainer id={}", id);
        return trainerService.findById(id);
    }

    public List<TrainerDto> getAllTrainers() {
        log.info("Facade: getAllTrainers");
        return trainerService.findAll();
    }

    public TrainingDto createTraining(TrainingDto dto) {
        log.info("Facade: createTraining");
        return trainingService.create(dto);
    }

    public TrainingDto getTraining(Long id) {
        log.info("Facade: getTraining id={}", id);
        return trainingService.findById(id);
    }

    public List<TrainingDto> getAllTrainings() {
        log.info("Facade: getAllTrainings");
        return trainingService.findAll();
    }
}
