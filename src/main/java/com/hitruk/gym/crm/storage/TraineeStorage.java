package com.hitruk.gym.crm.storage;

import com.hitruk.gym.crm.model.entity.Trainee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TraineeStorage implements Storage<Long, Trainee> {
    private static final Class<Trainee> TRAINEE_TYPE = Trainee.class;

    private final Map<Long, Trainee> trainees = new HashMap<>();
    private IdGenerator idGenerator;

    @Autowired
    public void setIdGenerator(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public Trainee findById(Long id) {
        return trainees.get(id);
    }

    @Override
    public List<Trainee> findAll() {
        return List.copyOf(trainees.values());
    }

    @Override
    public Trainee create(Trainee entity) {
        Long id = idGenerator.generate(TRAINEE_TYPE);
        entity.setId(id);
        trainees.put(id, entity);
        log.debug("Trainee stored: id={}", id);
        return entity;
    }

    @Override
    public Trainee update(Long id, Trainee entity) {
        trainees.put(id, entity);
        log.debug("Trainee updated: id={}", id);
        return entity;
    }

    @Override
    public boolean delete(Long id) {
        boolean removed = trainees.remove(id) != null;
        log.debug("Trainee delete: id={}, removed={}", id, removed);
        return removed;
    }
}
