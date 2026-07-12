package com.hitruk.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.model.dao.TraineeDao;
import com.hitruk.gym.crm.model.dao.TrainerDao;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.service.TraineeServiceImpl;
import com.hitruk.gym.crm.util.ProfileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;
    @Mock
    private TrainerDao trainerDao;
    @Mock
    private TraineeMapper traineeMapper;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private TrainingMapper trainingMapper;
    @Mock
    private ProfileGenerator profileGenerator;

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee trainee;
    private TraineeDto traineeDto;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .id(1L).firstName("John").lastName("Smith")
                .username("John.Smith").password("pass123456").isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1)).address("New York").build();

        traineeDto = TraineeDto.builder()
                .id(1L).firstName("John").lastName("Smith")
                .username("John.Smith").password("pass123456").isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1)).address("New York").build();
    }

    @Test
    void create_buildsUserWithGeneratedCredentials() {
        TraineeDto input = TraineeDto.builder().firstName("Jane").lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, Month.MAY, 5)).address("LA").build();

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(profileGenerator.generateUsername("Jane", "Doe", List.of())).thenReturn("Jane.Doe");
        when(profileGenerator.generatePassword()).thenReturn("abc1234xyz");
        when(traineeDao.save(any(Trainee.class))).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.create(input);

        assertNotNull(result);
        verify(profileGenerator).generatePassword();
        verify(traineeDao).save(argThat(t ->
                "Jane.Doe".equals(t.getUsername()) &&
                        "abc1234xyz".equals(t.getPassword()) &&
                        Boolean.TRUE.equals(t.getIsActive())
        ));
    }

    @Test
    void create_collectsUsernamesFromBothTraineesAndTrainers() {
        Trainer existingTrainer = Trainer.builder().username("Existing.Trainer").build();

        TraineeDto input = TraineeDto.builder().firstName("Jane").lastName("Doe").build();

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of(existingTrainer));
        when(profileGenerator.generateUsername(eq("Jane"), eq("Doe"), anyList())).thenReturn("Jane.Doe1");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(traineeDao.save(any())).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        service.create(input);

        verify(profileGenerator).generateUsername(eq("Jane"), eq("Doe"),
                argThat(list -> list.contains("Existing.Trainer")));
    }

    @Test
    void matchCredentials_delegatesToDao() {
        when(traineeDao.matchCredentials("John.Smith", "pass123456")).thenReturn(true);

        assertTrue(service.matchCredentials("John.Smith", "pass123456"));
    }

    @Test
    void matchCredentials_invalidCredentials_returnsFalse() {
        when(traineeDao.matchCredentials("John.Smith", "wrongpass")).thenReturn(false);

        assertFalse(service.matchCredentials("John.Smith", "wrongpass"));
    }

    @Test
    void findByUsername_existing_returnsDto() {
        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.findByUsername("John.Smith");

        assertEquals("John.Smith", result.getUsername());
        verify(traineeDao).findByUsername("John.Smith");
    }

    @Test
    void findByUsername_notFound_throwsEntityNotFoundException() {
        when(traineeDao.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUsername("Unknown"));
    }

    @Test
    void changePassword_validOldPassword_delegatesChange() {
        when(traineeDao.matchCredentials("John.Smith", "pass123456")).thenReturn(true);

        service.changePassword("John.Smith", "pass123456", "newPass99");

        verify(traineeDao).changePassword("John.Smith", "newPass99");
    }

    @Test
    void changePassword_invalidOldPassword_throwsException() {
        when(traineeDao.matchCredentials("John.Smith", "wrongOld")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.changePassword("John.Smith", "wrongOld", "newPass"));
        verify(traineeDao, never()).changePassword(any(), any());
    }

    @Test
    void update_existingTrainee_updatesUserFields() {
        TraineeDto updateDto = TraineeDto.builder()
                .username("John.Smith").firstName("Johnny").lastName("Smyth")
                .isActive(false).dateOfBirth(LocalDate.of(1991, Month.FEBRUARY, 2)).address("Boston")
                .build();

        when(traineeDao.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        service.update(updateDto);

        assertEquals("Johnny", trainee.getFirstName());
        assertEquals("Smyth", trainee.getLastName());
        assertFalse(trainee.getIsActive());
        assertEquals(LocalDate.of(1991, Month.FEBRUARY, 2), trainee.getDateOfBirth());
        assertEquals("Boston", trainee.getAddress());
    }

    @Test
    void update_notFound_throwsEntityNotFoundException() {
        when(traineeDao.findByUsername("Unknown")).thenReturn(Optional.empty());
        TraineeDto dto = TraineeDto.builder().username("Unknown").build();

        assertThrows(EntityNotFoundException.class, () -> service.update(dto));
        verify(traineeDao, never()).update(any());
    }

    @Test
    void setActive_delegatesToDao() {
        service.setActive("John.Smith", false);

        verify(traineeDao).setActive("John.Smith", false);
    }

    @Test
    void deleteByUsername_delegatesToDao() {
        service.deleteByUsername("John.Smith");

        verify(traineeDao).deleteByUsername("John.Smith");
    }

    @Test
    void getTrainings_returnsTrainingDtos() {
        Training training = Training.builder().id(1L).name("Session").build();
        TrainingDto dto = TrainingDto.builder().id(1L).name("Session").build();

        when(traineeDao.getTrainings("John.Smith", null, null, null, null))
                .thenReturn(List.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = service.getTrainings("John.Smith", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Session", result.get(0).getName());
    }

    @Test
    void getUnassignedTrainers_returnsTrainerDtos() {
        Trainer trainer = Trainer.builder().username("Bob.Builder").build();
        TrainerDto trainerDto = TrainerDto.builder().username("Bob.Builder").build();

        when(traineeDao.getUnassignedTrainers("John.Smith")).thenReturn(List.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        List<TrainerDto> result = service.getUnassignedTrainers("John.Smith");

        assertEquals(1, result.size());
        assertEquals("Bob.Builder", result.get(0).getUsername());
    }

    @Test
    void updateTrainers_returnsUpdatedTrainerDtos() {
        Trainer trainer = Trainer.builder().username("Bob.Builder").build();
        TrainerDto trainerDto = TrainerDto.builder().username("Bob.Builder").build();

        when(traineeDao.updateTrainers("John.Smith", List.of("Bob.Builder"))).thenReturn(List.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        List<TrainerDto> result = service.updateTrainers("John.Smith", List.of("Bob.Builder"));

        assertEquals(1, result.size());
        assertEquals("Bob.Builder", result.get(0).getUsername());
    }
}