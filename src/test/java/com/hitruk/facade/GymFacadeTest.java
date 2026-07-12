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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {

    @Mock private TraineeService traineeService;
    @Mock private TrainerService trainerService;
    @Mock private TrainingService trainingService;

    private GymFacade facade;

    @BeforeEach
    void setUp() {
        facade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void createTrainer_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().firstName("Chris").specialization("FITNESS").build();
        TrainerDto created = TrainerDto.builder().id(1L).username("Chris.Bumstead").build();
        when(trainerService.create(dto)).thenReturn(created);

        TrainerDto result = facade.createTrainer(dto);

        assertEquals(1L, result.getId());
        verify(trainerService).create(dto);
    }

    @Test
    void trainerMatchCredentials_delegatesAndReturnsResult() {
        when(trainerService.matchCredentials("Chris.Bumstead", "pass")).thenReturn(true);

        assertTrue(facade.trainerMatchCredentials("Chris.Bumstead", "pass"));
        verify(trainerService).matchCredentials("Chris.Bumstead", "pass");
    }

    @Test
    void getTrainerByUsername_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().username("Chris.Bumstead").build();
        when(trainerService.findByUsername("Chris.Bumstead")).thenReturn(dto);

        assertEquals("Chris.Bumstead", facade.getTrainerByUsername("Chris.Bumstead").getUsername());
    }

    @Test
    void changeTrainerPassword_delegatesToTrainerService() {
        facade.changeTrainerPassword("Chris.Bumstead", "old", "new");

        verify(trainerService).changePassword("Chris.Bumstead", "old", "new");
    }

    @Test
    void updateTrainer_delegatesToTrainerService() {
        TrainerDto dto = TrainerDto.builder().username("Chris.Bumstead").specialization("FITNESS").build();
        when(trainerService.update(dto)).thenReturn(dto);

        facade.updateTrainer(dto);

        verify(trainerService).update(dto);
    }

    @Test
    void setTrainerActive_delegatesToTrainerService() {
        facade.setTrainerActive("Chris.Bumstead", false);

        verify(trainerService).setActive("Chris.Bumstead", false);
    }

    @Test
    void getAllTrainers_delegatesToTrainerService() {
        when(trainerService.findAll()).thenReturn(List.of(TrainerDto.builder().build()));

        assertEquals(1, facade.getAllTrainers().size());
        verify(trainerService).findAll();
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
    void traineeMatchCredentials_delegatesAndReturnsResult() {
        when(traineeService.matchCredentials("Jane.Doe", "pass")).thenReturn(true);

        assertTrue(facade.traineeMatchCredentials("Jane.Doe", "pass"));
    }

    @Test
    void getTraineeByUsername_delegatesToTraineeService() {
        TraineeDto dto = TraineeDto.builder().username("Jane.Doe").build();
        when(traineeService.findByUsername("Jane.Doe")).thenReturn(dto);

        assertEquals("Jane.Doe", facade.getTraineeByUsername("Jane.Doe").getUsername());
    }

    @Test
    void changeTraineePassword_delegatesToTraineeService() {
        facade.changeTraineePassword("Jane.Doe", "old", "new");

        verify(traineeService).changePassword("Jane.Doe", "old", "new");
    }

    @Test
    void updateTrainee_delegatesToTraineeService() {
        TraineeDto dto = TraineeDto.builder().username("Jane.Doe").build();
        when(traineeService.update(dto)).thenReturn(dto);

        facade.updateTrainee(dto);

        verify(traineeService).update(dto);
    }

    @Test
    void setTraineeActive_delegatesToTraineeService() {
        facade.setTraineeActive("Jane.Doe", true);

        verify(traineeService).setActive("Jane.Doe", true);
    }

    @Test
    void deleteTrainee_delegatesToTraineeService() {
        facade.deleteTrainee("Jane.Doe");

        verify(traineeService).deleteByUsername("Jane.Doe");
    }

    @Test
    void getTraineeTrainings_delegatesToTraineeService() {
        when(traineeService.getTrainings("Jane.Doe", null, null, null, null))
                .thenReturn(List.of(TrainingDto.builder().build()));

        assertEquals(1, facade.getTraineeTrainings("Jane.Doe", null, null, null, null).size());
    }

    @Test
    void getUnassignedTrainers_delegatesToTraineeService() {
        when(traineeService.getUnassignedTrainers("Jane.Doe"))
                .thenReturn(List.of(TrainerDto.builder().build()));

        assertEquals(1, facade.getUnassignedTrainers("Jane.Doe").size());
    }

    @Test
    void updateTraineeTrainers_delegatesToTraineeService() {
        List<String> usernames = List.of("Chris.Bumstead");
        when(traineeService.updateTrainers("Jane.Doe", usernames))
                .thenReturn(List.of(TrainerDto.builder().username("Chris.Bumstead").build()));

        List<TrainerDto> result = facade.updateTraineeTrainers("Jane.Doe", usernames);

        assertEquals(1, result.size());
        verify(traineeService).updateTrainers("Jane.Doe", usernames);
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
}