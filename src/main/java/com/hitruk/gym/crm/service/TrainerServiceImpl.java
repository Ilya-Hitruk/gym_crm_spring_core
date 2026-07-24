package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
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
    private final TrainerRepository trainerRepository;
    private final TraineeRepository traineeRepository;
    private final TrainingTypeRepository trainingTypeRepository;
    private final TrainerMapper trainerMapper;
    private final ProfileGenerator profileGenerator;

    @Override
    public TrainerDto create(TrainerDto dto) {
        log.info("Creating trainer: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());
        List<String> allUsernames = new ArrayList<>();
        trainerRepository.findAll().forEach(t -> allUsernames.add(t.getUsername()));
        traineeRepository.findAll().forEach(t -> allUsernames.add(t.getUsername()));

        String username = profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), allUsernames);
        String password = profileGenerator.generatePassword();

        TrainingType specialization = trainingTypeRepository.findByName(dto.getSpecialization())
                .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getSpecialization()));

        Trainer trainer = Trainer.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .specialization(specialization)
                .build();

        Trainer saved = trainerRepository.save(trainer);
        log.info("Trainer created: username={}", username);
        return trainerMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        log.info("Matching credentials for trainer: username={}", username);
        return trainerRepository.matchCredentials(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public TrainerDto findByUsername(String username) {
        log.info("Finding trainer by username={}", username);
        return trainerRepository.findByUsername(username)
                .map(trainerMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Trainer not found: username={}", username);
                    return new EntityNotFoundException("Trainer not found: " + username);
                });
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for trainer: username={}", username);
        if (!trainerRepository.matchCredentials(username, oldPassword)) {
            throw new EntityNotFoundException("Invalid credentials for trainer: " + username);
        }
        trainerRepository.changePassword(username, newPassword);
    }

    @Override
    public TrainerDto update(TrainerDto dto) {
        log.info("Updating trainer: username={}", dto.getUsername());
        Trainer existing = trainerRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + dto.getUsername()));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setIsActive(dto.getIsActive());

        if (dto.getSpecialization() != null) {
            TrainingType specialization = trainingTypeRepository.findByName(dto.getSpecialization())
                    .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getSpecialization()));
            existing.setSpecialization(specialization);
        }

        Trainer updated = trainerRepository.update(existing);
        log.info("Trainer updated: username={}", dto.getUsername());
        return trainerMapper.toDto(updated);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        log.info("Setting trainer isActive={} for username={}", isActive, username);
        trainerRepository.setActive(username, isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> findAll() {
        log.info("Finding all trainers");
        return trainerRepository.findAll().stream().map(trainerMapper::toDto).toList();
    }
}
