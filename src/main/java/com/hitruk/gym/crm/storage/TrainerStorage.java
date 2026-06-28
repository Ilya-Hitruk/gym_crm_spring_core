package com.hitruk.gym.crm.storage;

import com.hitruk.gym.crm.model.entity.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TrainerStorage implements Storage<Long, Trainer> {
    private static final Class<Trainer> TRAINER_TYPE = Trainer.class;

    private final Map<Long, Trainer> trainers = new HashMap<>();
    private IdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Trainer findById(Long id) {
        return trainers.get(id);
    }

    @Override
    public List<Trainer> findAll() {
        return List.copyOf(trainers.values());
    }

    @Override
    public Trainer create(Trainer entity) {
        Long id = idGenerator.generate(TRAINER_TYPE);
        entity.setId(id);
        trainers.put(id, entity);
        log.debug("Trainer stored: id={}", id);
        return entity;
    }

    @Override
    public Trainer update(Long id, Trainer entity) {
        entity.setId(id);
        trainers.put(id, entity);
        log.debug("Trainer updated: id={}", id);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        boolean removed = trainers.remove(id) != null;
        log.debug("Trainer delete: id={}, removed={}", id, removed);
        return removed;
    }
}
