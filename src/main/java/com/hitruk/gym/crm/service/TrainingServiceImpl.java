package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.model.dao.TraineeDao;
import com.hitruk.gym.crm.model.dao.TrainerDao;
import com.hitruk.gym.crm.model.dao.TrainingDao;
import com.hitruk.gym.crm.model.dao.TrainingTypeDao;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {
    private final TrainingDao trainingDao;
    private final TraineeDao traineeDao;
    private final TrainerDao trainerDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainingMapper trainingMapper;

    @Override
    public TrainingDto create(TrainingDto dto) {
        log.info("Creating training: name={}", dto.getName());

        Trainee trainee = traineeDao.findByUsername(dto.getTraineeUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + dto.getTraineeUsername()));

        Trainer trainer = trainerDao.findByUsername(dto.getTrainerUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + dto.getTrainerUsername()));

        TrainingType type = trainingTypeDao.findByName(dto.getTrainingType())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getTrainingType()));

        Training training = Training.builder()
                .trainee(trainee)
                .trainer(trainer)
                .name(dto.getName())
                .type(type)
                .date(dto.getDate())
                .duration(dto.getDuration())
                .build();

        Training saved = trainingDao.save(training);
        log.info("Training created: id={}, name={}", saved.getId(), saved.getName());
        return trainingMapper.toDto(saved);
    }
}
