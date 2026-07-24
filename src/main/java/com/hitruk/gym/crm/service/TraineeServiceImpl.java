package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Trainee;
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
public class TraineeServiceImpl implements TraineeService {
    private final TraineeRepository traineeRepository;
    private final TrainerRepository trainerRepository;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final ProfileGenerator profileGenerator;

    @Override
    public TraineeDto create(TraineeDto dto) {
        log.info("Creating trainee: firstName={}, lastName={}", dto.getFirstName(), dto.getLastName());
        List<String> allUsernames = new ArrayList<>();
        traineeRepository.findAll().forEach(t -> allUsernames.add(t.getUsername()));
        trainerRepository.findAll().forEach(t -> allUsernames.add(t.getUsername()));

        String username = profileGenerator.generateUsername(dto.getFirstName(), dto.getLastName(), allUsernames);
        String password = profileGenerator.generatePassword();

        Trainee trainee = Trainee.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .dateOfBirth(dto.getDateOfBirth())
                .address(dto.getAddress())
                .build();

        Trainee saved = traineeRepository.save(trainee);
        log.info("Trainee created: username={}", username);
        return traineeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean matchCredentials(String username, String password) {
        log.info("Matching credentials for trainee: username={}", username);
        return traineeRepository.matchCredentials(username, password);
    }

    @Override
    @Transactional(readOnly = true)
    public TraineeDto findByUsername(String username) {
        log.info("Finding trainee by username={}", username);
        return traineeRepository.findByUsername(username)
                .map(traineeMapper::toDto)
                .orElseThrow(() -> {
                    log.warn("Trainee not found: username={}", username);
                    return new EntityNotFoundException("Trainee not found: " + username);
                });
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        log.info("Changing password for trainee: username={}", username);
        if (!traineeRepository.matchCredentials(username, oldPassword)) {
            throw new EntityNotFoundException("Invalid credentials for trainee: " + username);
        }
        traineeRepository.changePassword(username, newPassword);
    }

    @Override
    public TraineeDto update(TraineeDto dto) {
        log.info("Updating trainee: username={}", dto.getUsername());
        Trainee existing = traineeRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found: " + dto.getUsername()));

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        existing.setIsActive(dto.getIsActive());
        existing.setDateOfBirth(dto.getDateOfBirth());
        existing.setAddress(dto.getAddress());

        Trainee updated = traineeRepository.update(existing);
        log.info("Trainee updated: username={}", dto.getUsername());
        return traineeMapper.toDto(updated);
    }

    @Override
    public void setActive(String username, boolean isActive) {
        log.info("Setting trainee isActive={} for username={}", isActive, username);
        traineeRepository.setActive(username, isActive);
    }

    @Override
    public void deleteByUsername(String username) {
        log.info("Deleting trainee: username={}", username);
        traineeRepository.deleteByUsername(username);
        log.info("Trainee deleted: username={}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingDto> getTrainings(String username, LocalDate fromDate, LocalDate toDate,
                                          String trainerName, String trainingType) {
        log.info("Getting trainings for trainee: username={}", username);
        return traineeRepository.getTrainings(username, fromDate, toDate, trainerName, trainingType)
                .stream().map(trainingMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainerDto> getUnassignedTrainers(String traineeUsername) {
        log.info("Getting unassigned trainers for trainee: username={}", traineeUsername);
        return traineeRepository.getUnassignedTrainers(traineeUsername)
                .stream().map(trainerMapper::toDto).toList();
    }

    @Override
    public List<TrainerDto> updateTrainers(String traineeUsername, List<String> trainerUsernames) {
        log.info("Updating trainers for trainee: username={}", traineeUsername);
        return traineeRepository.updateTrainers(traineeUsername, trainerUsernames)
                .stream().map(trainerMapper::toDto).toList();
    }
}
