package com.hitruk.gym.crm.storage;

import com.hitruk.gym.crm.model.entity.Training;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TrainingStorage implements Storage<Long, Training> {
    private static final Class<Training> TRAINING_TYPE = Training.class;

    private final Map<Long, Training> trainings = new HashMap<>();
    private IdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Training findById(Long id) {
        return trainings.get(id);
    }

    @Override
    public List<Training> findAll() {
        return List.copyOf(trainings.values());
    }

    @Override
    public Training create(Training entity) {
        Long id = idGenerator.generate(TRAINING_TYPE);
        entity.setId(id);
        trainings.put(id, entity);
        log.debug("Training created: id={}, name={}", id, entity.getName());
        return entity;
    }

    @Override
    public Training update(Long id, Training entity) {
        entity.setId(id);
        trainings.put(id, entity);
        log.debug("Training updated: id={}", id);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        boolean removed = trainings.remove(id) != null;
        log.debug("Training delete: id={}, removed={}", id, removed);
        return removed;
    }
}
