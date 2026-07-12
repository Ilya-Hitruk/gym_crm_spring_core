package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.model.dao.TraineeDao;
import com.hitruk.gym.crm.model.dao.TrainerDao;
import com.hitruk.gym.crm.model.dao.TrainingTypeDao;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.util.ProfileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {
    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;
    private final TrainingTypeDao trainingTypeDao;
    private final TrainerMapper trainerMapper;
    private final ProfileGenerator profileGenerator;

    @Override
    public TrainerDto create(TrainerDto dto) {
        log.info("Creating trainer: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());
        List<String> allUsernames = new ArrayList<>();
        trainerDao.findAll().forEach(t -> allUsernames.add(t.getUsername()));
        traineeDao.findAll().forEach(t -> allUsernames.add(t.getUsername()));

        String username = profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), allUsernames);
        String password = profileGenerator.generatePassword();

        TrainingType specialization = trainingTypeDao.findByName(dto.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getSpecialization()));

        Trainer trainer = Trainer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .specialization(specialization)
                .build();

        Trainer saved = trainerDao.save(trainer);
        log.info("Trainer created: username={}", username);
        return trainerMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        log.info("Matching credentials for trainer: username={}", username);
        return trainerDao.matchCredentials(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDto findByUsername(String username) {
        log.info("Finding trainer by username={}", username);
        return trainerDao.findByUsername(username)
                .map(trainerMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: username={}", username);
                    return new EntityNotFoundException("Trainer not found: " + username);
                });
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for trainer: username={}", username);
        if (!trainerDao.matchCredentials(username, oldPassword)) {
            throw new EntityNotFoundException("Invalid credentials for trainer: " + username);
        }
        trainerDao.changePassword(username, newPassword);
    }

    @Override
    public TrainerDto update(TrainerDto dto) {
        log.info("Updating trainer: username={}", dto.getUsername());
        Trainer existing = trainerDao.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + dto.getUsername()));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setIsActive(dto.getIsActive());

        if (dto.getSpecialization() != null) {
            TrainingType specialization = trainingTypeDao.findByName(dto.getSpecialization())
                    .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getSpecialization()));
            existing.setSpecialization(specialization);
        }

        Trainer updated = trainerDao.update(existing);
        log.info("Trainer updated: username={}", dto.getUsername());
        return trainerMapper.toDto(updated);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        log.info("Setting trainer isActive={} for username={}", isActive, username);
        trainerDao.setActive(username, isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> findAll() {
        log.info("Finding all trainers");
        return trainerDao.findAll().stream().map(trainerMapper::toDto).toList();
    }
}
