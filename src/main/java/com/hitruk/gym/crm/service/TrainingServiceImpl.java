package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Training;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TrainingServiceImpl implements TrainingService {
    private Dao<Long, Training> trainingDao;
    private Mapper<Training, TrainingDto> trainingMapper;

    @Autowired
    public void setTrainingDao(Dao<Long, Training> trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Autowired
    public void setTrainingMapper(Mapper<Training, TrainingDto> trainingMapper) {
        this.trainingMapper = trainingMapper;
    }

    @Override
    public TrainingDto findById(Long id) {
        log.info("Finding training by id={}", id);
        return trainingDao.findById(id)
                .map(trainingMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Training not found: id={}", id);
                    return new EntityNotFoundException("Training not found by id: " + id);
                });
    }

    @Override
    public List<TrainingDto> findAll() {
        log.info("Finding all trainings");
        return trainingDao.findAll().stream().map(trainingMapper::toDto).toList();
    }

    @Override
    public TrainingDto create(TrainingDto dto) {
        log.info("Creating training: name={}", dto.getName());
        Training created = trainingDao.create(trainingMapper.toEntity(dto));
        log.info("Training created: id={}", created.getId());
        return trainingMapper.toDto(created);
    }
}
