package com.hitruk.facade;

import com.hitruk.gym.crm.facade.GymFacade;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.service.TraineeService;
import com.hitruk.gym.crm.service.TrainerService;
import com.hitruk.gym.crm.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainee_delegatesToTraineeService() {
        TraineeDto dto = TraineeDto.builder().firstName("Jane").lastName("Doe").build();
        TraineeDto created = TraineeDto.builder().id(1L).username("Jane.Doe").build();
        when(traineeService.create(dto)).thenReturn(created);

        TraineeDto result = facade.createTrainee(dto);

        assertEquals(1L, result.getId());
        verify(traineeService).create(dto);
    }

    @Test
    void updateTrainee_delegatesToTraineeService() {
        TraineeDto dto = TraineeDto.builder().id(1L).firstName("Updated").build();
        when(traineeService.update(dto)).thenReturn(dto);

        facade.updateTrainee(dto);

        verify(traineeService).update(dto);
    }

    @Test
    void deleteTrainee_existingId_returnsTrue() {
        when(traineeService.delete(1L)).thenReturn(true);

        assertTrue(facade.deleteTrainee(1L));
        verify(traineeService).delete(1L);
    }

    @Test
    void deleteTrainee_nonExistingId_returnsFalse() {
        when(traineeService.delete(99L)).thenReturn(false);

        assertFalse(facade.deleteTrainee(99L));
    }

    @Test
    void getTrainee_delegatesToTraineeService() {
        TraineeDto dto = TraineeDto.builder().id(1L).build();
        when(traineeService.findById(1L)).thenReturn(dto);

        TraineeDto result = facade.getTrainee(1L);

        assertEquals(1L, result.getId());
        verify(traineeService).findById(1L);
    }

    @Test
    void getAllTrainees_delegatesToTraineeService() {
        when(traineeService.findAll()).thenReturn(List.of(TraineeDto.builder().build()));

        List<TraineeDto> result = facade.getAllTrainees();

        assertEquals(1, result.size());
        verify(traineeService).findAll();
    }

    @Test
    void createTrainer_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().firstName("Chris").lastName("Bumstead").specialization("BODYBUILDING").build();
        TrainerDto created = TrainerDto.builder().id(1L).username("Chris.Bumstead").build();
        when(trainerService.create(dto)).thenReturn(created);

        TrainerDto result = facade.createTrainer(dto);

        assertEquals(1L, result.getId());
        verify(trainerService).create(dto);
    }

    @Test
    void updateTrainer_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().id(1L).specialization("FITNESS").build();
        when(trainerService.update(dto)).thenReturn(dto);

        facade.updateTrainer(dto);

        verify(trainerService).update(dto);
    }

    @Test
    void getTrainer_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().id(1L).build();
        when(trainerService.findById(1L)).thenReturn(dto);

        assertEquals(1L, facade.getTrainer(1L).getId());
        verify(trainerService).findById(1L);
    }

    @Test
    void getAllTrainers_delegatesToTrainerService() {
        when(trainerService.findAll()).thenReturn(List.of(TrainerDto.builder().build()));

        assertEquals(1, facade.getAllTrainers().size());
        verify(trainerService).findAll();
    }

    @Test
    void createTraining_delegatesToTrainingService() {
        TrainingDto dto = TrainingDto.builder().name("Session").build();
        TrainingDto created = TrainingDto.builder().id(1L).build();
        when(trainingService.create(dto)).thenReturn(created);

        TrainingDto result = facade.createTraining(dto);

        assertEquals(1L, result.getId());
        verify(trainingService).create(dto);
    }

    @Test
    void getTraining_delegatesToTrainingService() {
        TrainingDto dto = TrainingDto.builder().id(1L).name("Cardio").build();
        when(trainingService.findById(1L)).thenReturn(dto);

        assertEquals("Cardio", facade.getTraining(1L).getName());
        verify(trainingService).findById(1L);
    }

    @Test
    void getAllTrainings_delegatesToTrainingService() {
        when(trainingService.findAll()).thenReturn(List.of(TrainingDto.builder().build()));

        assertEquals(1, facade.getAllTrainings().size());
        verify(trainingService).findAll();
    }
}
