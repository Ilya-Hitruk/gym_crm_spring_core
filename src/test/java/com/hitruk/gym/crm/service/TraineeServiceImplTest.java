package com.hitruk.gym.crm.service;

import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.exception.InvalidActivationStateException;
import com.hitruk.gym.crm.exception.InvalidCredentialsException;
import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.api.dto.TraineeDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.TrainingDto;
import com.hitruk.gym.crm.entity.Trainee;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.Training;
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
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;
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
                .credentials(UserCredentials.of("John.Smith", "pass123456")).isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1)).address("New York").build();
    }

    @Test
    void create_buildsUserWithGeneratedCredentials() {
        TraineeDto input = TraineeDto.builder().firstName("Jane").lastName("Doe")
                .dateOfBirth(LocalDate.of(1995, Month.MAY, 5)).address("LA").build();

        when(trainerRepository.existsByFirstNameAndLastName("Jane", "Doe")).thenReturn(false);
        when(traineeRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of());
        when(trainerRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of());
        when(profileGenerator.generateUsername(eq("Jane"), eq("Doe"), anyList())).thenReturn("Jane.Doe");
        when(profileGenerator.generatePassword()).thenReturn("abc1234xyz");
        when(traineeRepository.save(any(Trainee.class))).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.create(input);

        assertNotNull(result);
        verify(profileGenerator).generatePassword();
        verify(traineeRepository).save(argThat(t ->
                "Jane.Doe".equals(t.getUsername()) &&
                        "abc1234xyz".equals(t.getPassword()) &&
                        Boolean.TRUE.equals(t.getIsActive())
        ));
    }

    @Test
    void create_collectsUsernamesFromBothTraineesAndTrainers() {
        TraineeDto input = TraineeDto.builder().firstName("Jane").lastName("Doe").build();

        when(trainerRepository.existsByFirstNameAndLastName("Jane", "Doe")).thenReturn(false);
        when(traineeRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of("Jane.Doe"));
        when(trainerRepository.findUsernamesStartingWith("Jane.Doe")).thenReturn(List.of("Jane.Doe1"));
        when(profileGenerator.generateUsername(eq("Jane"), eq("Doe"), anyList())).thenReturn("Jane.Doe2");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(traineeRepository.save(any())).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        service.create(input);

        verify(profileGenerator).generateUsername(eq("Jane"), eq("Doe"),
                argThat(list -> list.contains("Jane.Doe") && list.contains("Jane.Doe1")));
    }

    @Test
    void create_personAlreadyRegisteredAsTrainer_throwsIllegalArgumentException() {
        TraineeDto input = TraineeDto.builder().firstName("Jane").lastName("Doe").build();

        when(trainerRepository.existsByFirstNameAndLastName("Jane", "Doe")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> service.create(input));
        verify(traineeRepository, never()).save(any());
    }

    @Test
    void matchCredentials_delegatesToDao() {
        when(traineeRepository.matchCredentials("John.Smith", "pass123456")).thenReturn(true);

        assertTrue(service.matchCredentials("John.Smith", "pass123456"));
    }

    @Test
    void matchCredentials_invalidCredentials_returnsFalse() {
        when(traineeRepository.matchCredentials("John.Smith", "wrongpass")).thenReturn(false);

        assertFalse(service.matchCredentials("John.Smith", "wrongpass"));
    }

    @Test
    void findByUsername_existing_returnsDto() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.findByUsername("John.Smith");

        assertEquals("John.Smith", result.getCredentials().getUsername());
        verify(traineeRepository).findByUsername("John.Smith");
    }

    @Test
    void findByUsername_notFound_throwsEntityNotFoundException() {
        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUsername("Unknown"));
    }

    @Test
    void changePassword_validOldPassword_delegatesChange() {
        when(traineeRepository.matchCredentials("John.Smith", "pass123456")).thenReturn(true);

        service.changePassword("John.Smith", "pass123456", "newPass99");

        verify(traineeRepository).changePassword("John.Smith", "newPass99");
    }

    @Test
    void changePassword_invalidOldPassword_throwsInvalidCredentialsException() {
        when(traineeRepository.matchCredentials("John.Smith", "wrongOld")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> service.changePassword("John.Smith", "wrongOld", "newPass"));
        verify(traineeRepository, never()).changePassword(any(), any());
    }

    @Test
    void update_existingTrainee_updatesUserFields() {
        TraineeDto updateDto = TraineeDto.builder()
                .credentials(UserCredentials.of("John.Smith", null)).firstName("Johnny").lastName("Smyth")
                .isActive(false).dateOfBirth(LocalDate.of(1991, Month.FEBRUARY, 2)).address("Boston")
                .build();

        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));
        when(traineeRepository.update(trainee)).thenReturn(trainee);
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
        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());
        TraineeDto dto = TraineeDto.builder().credentials(UserCredentials.of("Unknown", null)).build();

        assertThrows(EntityNotFoundException.class, () -> service.update(dto));
        verify(traineeRepository, never()).update(any());
    }

    @Test
    void setActive_stateChanges_delegatesToDao() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));

        service.setActive("John.Smith", false);

        verify(traineeRepository).setActive("John.Smith", false);
    }

    @Test
    void setActive_alreadyInRequestedState_throwsInvalidActivationStateException() {
        when(traineeRepository.findByUsername("John.Smith")).thenReturn(Optional.of(trainee));

        assertThrows(InvalidActivationStateException.class, () -> service.setActive("John.Smith", true));
        verify(traineeRepository, never()).setActive(any(), anyBoolean());
    }

    @Test
    void setActive_notFound_throwsEntityNotFoundException() {
        when(traineeRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.setActive("Unknown", true));
    }

    @Test
    void deleteByUsername_delegatesToDao() {
        service.deleteByUsername("John.Smith");

        verify(traineeRepository).deleteByUsername("John.Smith");
    }

    @Test
    void getTrainings_returnsTrainingDtos() {
        Training training = Training.builder().id(1L).name("Session").build();
        TrainingDto dto = TrainingDto.builder().id(1L).name("Session").build();

        when(traineeRepository.getTrainings("John.Smith", null, null, null, null))
                .thenReturn(List.of(training));
        when(trainingMapper.toDto(training)).thenReturn(dto);

        List<TrainingDto> result = service.getTrainings("John.Smith", null, null, null, null);

        assertEquals(1, result.size());
        assertEquals("Session", result.get(0).getName());
    }

    @Test
    void getUnassignedTrainers_returnsTrainerSummaries() {
        Trainer trainer = Trainer.builder().username("Bob.Builder").build();
        TrainerSummary summary = TrainerSummary.builder().username("Bob.Builder").build();

        when(traineeRepository.getUnassignedTrainers("John.Smith")).thenReturn(List.of(trainer));
        when(trainerMapper.toSummary(trainer)).thenReturn(summary);

        List<TrainerSummary> result = service.getUnassignedTrainers("John.Smith");

        assertEquals(1, result.size());
        assertEquals("Bob.Builder", result.get(0).getUsername());
    }

    @Test
    void updateTrainers_returnsUpdatedTrainerSummaries() {
        Trainer trainer = Trainer.builder().username("Bob.Builder").build();
        TrainerSummary summary = TrainerSummary.builder().username("Bob.Builder").build();

        when(traineeRepository.updateTrainers("John.Smith", List.of("Bob.Builder"))).thenReturn(List.of(trainer));
        when(trainerMapper.toSummary(trainer)).thenReturn(summary);

        List<TrainerSummary> result = service.updateTrainers("John.Smith", List.of("Bob.Builder"));

        assertEquals(1, result.size());
        assertEquals("Bob.Builder", result.get(0).getUsername());
    }
}
