package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
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
        return trainingMapper.toDto(saved);
    }
}
