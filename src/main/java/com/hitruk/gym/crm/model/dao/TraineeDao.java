package com.hitruk.gym.crm.model.dao;

import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.storage.TraineeStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TraineeDao implements Dao<Long, Trainee> {
    private TraineeStorage traineeStorage;

    @Autowired
    public void setTraineeStorage(TraineeStorage traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        log.debug("Finding trainee by id={}", id);
        return Optional.ofNullable(traineeStorage.findById(id));
    }

    @Override
    public List<Trainee> findAll() {
        log.debug("Finding all trainees");
        return traineeStorage.findAll();
    }

    @Override
    public Trainee create(Trainee entity) {
        log.debug("Creating trainee: firstName={}, lastName={}", entity.getFirstName(), entity.getLastName());
        return traineeStorage.create(entity);
    }

    @Override
    public Trainee update(Trainee entity) {
        log.debug("Updating trainee: id={}", entity.getId());
        return traineeStorage.update(entity.getId(), entity);
    }

    @Override
    public boolean delete(Long id) {
        log.debug("Deleting trainee: id={}", id);
        return traineeStorage.delete(id);
    }
}
