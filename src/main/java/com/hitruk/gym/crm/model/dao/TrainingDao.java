package com.hitruk.gym.crm.model.dao;

import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.storage.TrainingStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TrainingDao implements Dao<Long, Training> {
    private TrainingStorage trainingStorage;

    @Autowired
    public void setTrainingStorage(TrainingStorage trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public Optional<Training> findById(Long id) {
        log.debug("Finding training by id={}", id);
        return Optional.ofNullable(trainingStorage.findById(id));
    }

    @Override
    public List<Training> findAll() {
        log.debug("Finding all trainings");
        return trainingStorage.findAll();
    }

    @Override
    public Training create(Training entity) {
        log.debug("Creating training: name={}", entity.getName());
        return trainingStorage.create(entity);
    }

    @Override
    public Training update(Training entity) {
        log.debug("Updating training: id={}", entity.getId());
        return trainingStorage.update(entity.getId(), entity);
    }

    @Override
    public boolean delete(Long id) {
        log.debug("Deleting training: id={}", id);
        return trainingStorage.delete(id);
    }
}
