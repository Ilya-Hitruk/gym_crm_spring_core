package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.client.TrainerWorkloadNotifier;
import com.hitruk.gym.crm.client.dto.ActionType;
import com.hitruk.gym.crm.entity.Trainee;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;
import com.hitruk.gym.crm.entity.TrainingType;
import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.exception.InvalidTrainingStateException;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.monitoring.metrics.GymMetrics;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.repository.TrainingRepository;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {

    @Mock
    private TrainingRepository trainingRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private GymMetrics gymMetrics;
    @Mock
    private TrainerWorkloadNotifier trainerWorkloadNotifier;

    @InjectMocks
    private TrainingServiceImpl service;

    private Trainee trainee;
    private Trainer trainer;
    private TrainingType trainingType;
    private Training training;
    private TrainingDto trainingDto;

    @BeforeEach
    void setUp() {
        trainingType = TrainingType.builder().id(1L).name("FITNESS").build();
        trainee = Trainee.builder().id(1L).username("John.Smith").build();
        trainer = Trainer.builder().id(1L).username("Chris.Bumstead")
                .firstName("Chris").lastName("Bumstead").isActive(true)
                .specialization(trainingType).build();

        training = Training.builder()
                .id(1L).trainee(trainee).trainer(trainer)
                .name("Morning Lift").type(trainingType)
                .date(LocalDate.of(2024, Month.JANUARY, 15)).duration(60).build();

        trainingDto = TrainingDto.builder()
                .id(1L).traineeUsername("John.Smith").trainerUsername("Chris.Bumstead")
                .name("Morning Lift").trainingType("FITNESS")
                .date(LocalDate.of(2024, Month.JANUARY, 15)).duration(60).build();
    }

    @Test
    void create_allEntitiesFound_persistsAndReturnsDto() {
        TrainingDto input = TrainingDto.builder()
                .traineeUsername("John.Smith").trainerUsername("Chris.Bumstead")
                .name("Morning Lift").trainingType("FITNESS")
                .date(LocalDate.of(2024, Month.JANUARY, 15)).duration(60).build();

        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Chris.Bumstead")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("FITNESS")).thenReturn(Optional.of(trainingType));
        when(trainingRepository.save(any(Training.class))).thenReturn(training);
        when(trainingMapper.toDto(training)).thenReturn(trainingDto);

        TrainingDto result = service.create(input);

        assertNotNull(result);
        assertEquals("Morning Lift", result.getName());
        verify(trainingRepository).save(argThat(t ->
                t.getTrainee() == trainee &&
                        t.getTrainer() == trainer &&
                        t.getType() == trainingType
        ));
        verify(gymMetrics).incrementTrainingCreated();
        verify(trainerWorkloadNotifier).notify(argThat(req ->
                req.getTrainerUsername().equals("Chris.Bumstead") &&
                        req.getTrainerFirstName().equals("Chris") &&
                        req.getTrainerLastName().equals("Bumstead") &&
                        req.getIsActive().equals(true) &&
                        req.getTrainingDate().equals(LocalDate.of(2024, Month.JANUARY, 15)) &&
                        req.getTrainingDuration().equals(60) &&
                        req.getActionType() == ActionType.ADD
        ));
    }

    @Test
    void create_traineeNotFound_throwsEntityNotFoundException() {
        TrainingDto input = TrainingDto.builder()
                .traineeUsername("Unknown").trainerUsername("Chris.Bumstead")
                .trainingType("FITNESS").build();

        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(input));
        verify(trainingRepository, never()).save(any());
        verifyNoInteractions(trainerWorkloadNotifier);
    }

    @Test
    void create_trainerNotFound_throwsEntityNotFoundException() {
        TrainingDto input = TrainingDto.builder()
                .traineeUsername("John.Smith").trainerUsername("Unknown")
                .trainingType("FITNESS").build();

        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(input));
        verify(trainingRepository, never()).save(any());
        verifyNoInteractions(trainerWorkloadNotifier);
    }

    @Test
    void create_trainingTypeNotFound_throwsEntityNotFoundException() {
        TrainingDto input = TrainingDto.builder()
                .traineeUsername("John.Smith").trainerUsername("Chris.Bumstead")
                .trainingType("UNKNOWN").build();

        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(trainerRepository.findByUsername("Chris.Bumstead")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(input));
        verify(trainingRepository, never()).save(any());
        verifyNoInteractions(trainerWorkloadNotifier);
    }

    @Test
    void delete_futureTraining_deletesAndNotifiesDelete() {
        Training futureTraining = Training.builder()
                .id(1L).trainee(trainee).trainer(trainer)
                .name("Morning Lift").type(trainingType)
                .date(LocalDate.now().plusDays(10)).duration(60).build();

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(futureTraining));

        service.delete(1L);

        verify(trainingRepository).delete(futureTraining);
        verify(trainerWorkloadNotifier).notify(argThat(req ->
                req.getTrainerUsername().equals("Chris.Bumstead") &&
                        req.getActionType() == ActionType.DELETE &&
                        req.getTrainingDuration().equals(60)
        ));
    }

    @Test
    void delete_pastTraining_throwsInvalidTrainingStateException() {
        Training pastTraining = Training.builder()
                .id(1L).trainee(trainee).trainer(trainer)
                .name("Morning Lift").type(trainingType)
                .date(LocalDate.now().minusDays(1)).duration(60).build();

        when(trainingRepository.findById(1L)).thenReturn(Optional.of(pastTraining));

        assertThrows(InvalidTrainingStateException.class, () -> service.delete(1L));
        verify(trainingRepository, never()).delete(any());
        verifyNoInteractions(trainerWorkloadNotifier);
    }

    @Test
    void delete_trainingNotFound_throwsEntityNotFoundException() {
        when(trainingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.delete(99L));
        verify(trainingRepository, never()).delete(any());
        verifyNoInteractions(trainerWorkloadNotifier);
    }
}
