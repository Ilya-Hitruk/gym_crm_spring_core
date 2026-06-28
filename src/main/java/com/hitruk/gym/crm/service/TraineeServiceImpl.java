package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.User;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TraineeServiceImpl implements TraineeService {
    private Dao<Long, Trainee> traineeDao;
    private Dao<Long, Trainer> trainerDao;
    private Mapper<Trainee, TraineeDto> traineeMapper;
    private ProfileGenerator profileGenerator;

    @Autowired
    public void setTraineeDao(Dao<Long, Trainee> traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setTrainerDao(Dao<Long, Trainer> trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeMapper(Mapper<Trainee, TraineeDto> traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setProfileGenerator(ProfileGenerator profileGenerator) {
        this.profileGenerator = profileGenerator;
    }

    @Override
    public TraineeDto findById(Long id) {
        log.info("Finding trainee by id={}", id);
        return traineeDao.findById(id)
                .map(traineeMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: id={}", id);
                    return new EntityNotFoundException("Trainee not found by id: " + id);
                });
    }

    @Override
    public List<TraineeDto> findAll() {
        log.info("Finding all trainees");
        return traineeDao.findAll().stream().map(traineeMapper::toDto).toList();
    }

    @Override
    public TraineeDto create(TraineeDto dto) {
        log.info("Creating trainee: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());

        List<User> allUsers = new ArrayList<>(traineeDao.findAll());
        allUsers.addAll(trainerDao.findAll());

        Trainee entity = traineeMapper.toEntity(dto);
        entity.setUsername(profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), allUsers));
        entity.setPassword(profileGenerator.generatePassword());
        entity.setIsActive(true);

        Trainee created = traineeDao.create(entity);
        log.info("Trainee created: id={}, username={}", created.getId(), created.getUsername());
        return traineeMapper.toDto(created);
    }

    @Override
    public TraineeDto update(TraineeDto dto) {
        log.info("Updating trainee: id={}", dto.getId());
        Trainee entity = traineeDao.findById(dto.getId())
                .orElseThrow(() -> {
                    log.warn("Trainee not found for update: id={}", dto.getId());
                    return new EntityNotFoundException("Trainee not found by id: " + dto.getId());
                });

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setIsActive(dto.getIsActive());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setAddress(dto.getAddress());

        Trainee updated = traineeDao.update(entity);
        log.info("Trainee updated: id={}", updated.getId());
        return traineeMapper.toDto(updated);
    }

    @Override
    public boolean delete(Long id) {
        log.info("Deleting trainee: id={}", id);
        boolean deleted = traineeDao.delete(id);
        if (deleted) {
            log.info("Trainee deleted: id={}", id);
        } else {
            log.warn("Trainee not found for deletion: id={}", id);
        }
        return deleted;
    }
}
