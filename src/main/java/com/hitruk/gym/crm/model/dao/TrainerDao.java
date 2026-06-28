package com.hitruk.gym.crm.model.dao;

import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.storage.TrainerStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
public class TrainerDao implements Dao<Long, Trainer> {
    private TrainerStorage trainerStorage;

    @Autowired
    public void setTrainerStorage(TrainerStorage trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        log.debug("Finding trainer by id={}", id);
        return Optional.ofNullable(trainerStorage.findById(id));
    }

    @Override
    public List<Trainer> findAll() {
        log.debug("Finding all trainers");
        return trainerStorage.findAll();
    }

    @Override
    public Trainer create(Trainer entity) {
        log.debug("Creating trainer: firstName={}, lastName={}", entity.getFirstName(), entity.getLastName());
        return trainerStorage.create(entity);
    }

    @Override
    public Trainer update(Trainer entity) {
        log.debug("Updating trainer: id={}", entity.getId());
        return trainerStorage.update(entity.getId(), entity);
    }

    @Override
    public boolean delete(Long id) {
        log.debug("Deleting trainer: id={}", id);
        return trainerStorage.delete(id);
    }
}
