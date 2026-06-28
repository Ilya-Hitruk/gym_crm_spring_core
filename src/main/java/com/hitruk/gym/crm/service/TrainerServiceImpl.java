package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import com.hitruk.gym.crm.model.entity.User;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TrainerServiceImpl implements TrainerService {
    private Dao<Long, Trainer> trainerDao;
    private Dao<Long, Trainee> traineeDao;
    private Mapper<Trainer, TrainerDto> trainerMapper;
    private ProfileGenerator profileGenerator;

    @Autowired
    public void setTrainerDao(Dao<Long, Trainer> trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(Dao<Long, Trainee> traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerMapper(Mapper<Trainer, TrainerDto> trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Autowired
    public void setProfileGenerator(ProfileGenerator profileGenerator) {
        this.profileGenerator = profileGenerator;
    }

    @Override
    public TrainerDto findById(Long id) {
        log.info("Finding trainer by id={}", id);
        return trainerDao.findById(id)
                .map(trainerMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: id={}", id);
                    return new EntityNotFoundException("Trainer not found by id: " + id);
                });
    }

    @Override
    public List<TrainerDto> findAll() {
        log.info("Finding all trainers");
        return trainerDao.findAll().stream().map(trainerMapper::toDto).toList();
    }

    @Override
    public TrainerDto create(TrainerDto dto) {
        log.info("Creating trainer: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        List<User> allUsers = new ArrayList<>(trainerDao.findAll());
        allUsers.addAll(traineeDao.findAll());

        Trainer entity = trainerMapper.toEntity(dto);
        entity.setUsername(profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), allUsers));
        entity.setPassword(profileGenerator.generatePassword());
        entity.setIsActive(true);

        Trainer created = trainerDao.create(entity);
        log.info("Trainer created: id={}, username={}", created.getId(), created.getUsername());
        return trainerMapper.toDto(created);
    }

    @Override
    public TrainerDto update(TrainerDto dto) {
        log.info("Updating trainer: id={}", dto.getId());
        Trainer entity = trainerDao.findById(dto.getId())
                .orElseThrow(() -> {
                    log.warn("Trainer not found for update: id={}", dto.getId());
                    return new EntityNotFoundException("Trainer not found by id: " + dto.getId());
                });

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setIsActive(dto.getIsActive());
        entity.setSpecialization(TrainerSpecialization.valueOf(dto.getSpecialization()));

        Trainer updated = trainerDao.update(entity);
        log.info("Trainer updated: id={}", updated.getId());
        return trainerMapper.toDto(updated);
    }
}
