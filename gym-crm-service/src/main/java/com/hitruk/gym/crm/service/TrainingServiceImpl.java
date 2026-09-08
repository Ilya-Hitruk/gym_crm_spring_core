package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.client.TrainerWorkloadNotifier;
import com.hitruk.gym.crm.client.dto.ActionType;
import com.hitruk.gym.crm.client.dto.TrainerWorkloadRequest;
import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.exception.InvalidTrainingStateException;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.repository.TrainingRepository;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.entity.Trainee;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;
import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.monitoring.metrics.GymMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingRepository trainingRepository;
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainingMapper trainingMapper;
    private final GymMetrics gymMetrics;
    private final TrainerWorkloadNotifier trainerWorkloadNotifier;

    @Override
    public TrainingDto create(TrainingDto dto) {
        log.info("Creating training: name={}", dto.getName());

        Trainee trainee = traineeRepository.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + dto.getTraineeUsername()));

        Trainer trainer = trainerRepository.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + dto.getTrainerUsername()));

        TrainingType type = trainingTypeRepository.findByName(dto.getTrainingType())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getTrainingType()));

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(dto.getName())
                .type(type)
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();

        Training saved = trainingRepository.save(training);
        gymMetrics.incrementTrainingCreated();
        log.info("Training created: id={}, name={}", saved.getId(), saved.getName());

        notifyWorkload(trainer, saved, ActionType.ADD);

        return trainingMapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Training training = trainingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Training not found: " + id));

        if (!training.getDate().isAfter(LocalDate.now())) {
            throw new InvalidTrainingStateException("Cannot cancel a training that has already taken place");
        }

        Trainer trainer = training.getTrainer();

        trainingRepository.delete(training);
        log.info("Training deleted: id={}, name={}", training.getId(), training.getName());

        notifyWorkload(trainer, training, ActionType.DELETE);
    }

    private void notifyWorkload(Trainer trainer, Training training, ActionType actionType) {
        trainerWorkloadNotifier.notify(TrainerWorkloadRequest.builder()
                .trainerUsername(trainer.getUsername())
                .trainerFirstName(trainer.getFirstName())
                .trainerLastName(trainer.getLastName())
                .isActive(trainer.getIsActive())
                .trainingDate(training.getDate())
                .trainingDuration(training.getDuration())
                .actionType(actionType)
                .build());
    }
}
