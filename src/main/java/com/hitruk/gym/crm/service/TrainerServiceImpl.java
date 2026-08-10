package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.exception.InvalidActivationStateException;
import com.hitruk.gym.crm.exception.InvalidCredentialsException;
import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.monitoring.metrics.GymMetrics;
import com.hitruk.gym.crm.util.ProfileGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
    private final TraineeMapper traineeMapper;
    private final TrainingMapper trainingMapper;
    private final ProfileGenerator profileGenerator;
    private final GymMetrics gymMetrics;

    @Override
    public TrainerDto create(TrainerDto dto) {
        log.info("Creating trainer: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());
        if (traineeRepository.existsByFirstNameAndLastName(dto.getFirstName(), dto.getLastName())) {
            throw new IllegalArgumentException(
                    "Person is already registered as a trainee: " + dto.getFirstName() + " " + dto.getLastName());
        }

        String prefix = dto.getFirstName() + "." + dto.getLastName();
        List<String> candidateUsernames = new ArrayList<>(trainerRepository.findUsernamesStartingWith(prefix));
        candidateUsernames.addAll(traineeRepository.findUsernamesStartingWith(prefix));

        String username = profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), candidateUsernames);
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
        gymMetrics.incrementTrainerRegistrations();
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
            throw new InvalidCredentialsException("Invalid credentials for trainer: " + username);
        }
        trainerRepository.changePassword(username, newPassword);
    }

    @Override
    public TrainerDto update(TrainerDto dto) {
        String username = dto.getCredentials().getUsername();
        log.info("Updating trainer: username={}", username);
        Trainer existing = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setIsActive(dto.getIsActive());

        if (dto.getSpecialization() != null) {
            TrainingType specialization = trainingTypeRepository.findByName(dto.getSpecialization())
                    .orElseThrow(() -> new EntityNotFoundException("TrainingType not found: " + dto.getSpecialization()));
            existing.setSpecialization(specialization);
        }

        Trainer updated = trainerRepository.update(existing);
        log.info("Trainer updated: username={}", username);
        return trainerMapper.toDto(updated);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        log.info("Setting trainer isActive={} for username={}", isActive, username);
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + username));
        if (Boolean.valueOf(isActive).equals(trainer.getIsActive())) {
            throw new InvalidActivationStateException(
                    "Trainer " + username + " is already " + (isActive ? "active" : "inactive"));
        }
        trainerRepository.setActive(username, isActive);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> findAll() {
        log.info("Finding all trainers");
        return trainerRepository.findAll().stream().map(trainerMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TraineeDto> getTrainees(String trainerUsername) {
        log.info("Getting trainees for trainer: username={}", trainerUsername);
        Trainer trainer = trainerRepository.findByUsername(trainerUsername)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found: " + trainerUsername));
        return trainer.getTrainees().stream().map(traineeMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDto> getTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        log.info("Getting trainings for trainer: username={}", username);
        return trainerRepository.getTrainings(username, fromDate, toDate, traineeName)
                .stream().map(trainingMapper::toDto).toList();
    }
}
