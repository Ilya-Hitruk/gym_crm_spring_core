package com.hitruk.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.repository.TraineeRepository;
import com.hitruk.gym.crm.repository.TrainerRepository;
import com.hitruk.gym.crm.repository.TrainingTypeRepository;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.service.TrainerServiceImpl;
import com.hitruk.gym.crm.util.ProfileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainerMapper trainerMapper;
    @Mock
    private ProfileGenerator profileGenerator;

    @InjectMocks
    private TrainerServiceImpl service;

    private Trainer trainer;
    private TrainerDto trainerDto;
    private TrainingType trainingType;

    @BeforeEach
    void setUp() {
        trainingType = TrainingType.builder().id(1L).name("FITNESS").build();
        trainer = Trainer.builder()
                .id(1L).firstName("Chris").lastName("Bumstead")
                .username("Chris.Bumstead").password("pass123456").isActive(true)
                .specialization(trainingType).build();

        trainerDto = TrainerDto.builder()
                .id(1L).firstName("Chris").lastName("Bumstead")
                .username("Chris.Bumstead").password("pass123456")
                .isActive(true).specialization("FITNESS").build();
    }

    @Test
    void create_buildsTrainerWithGeneratedCredentials() {
        TrainerDto input = TrainerDto.builder()
                .firstName("Arnold").lastName("Schwarzenegger").specialization("FITNESS").build();

        when(trainerRepository.findAll()).thenReturn(List.of());
        when(traineeRepository.findAll()).thenReturn(List.of());
        when(profileGenerator.generateUsername("Arnold", "Schwarzenegger", List.of()))
                .thenReturn("Arnold.Schwarzenegger");
        when(profileGenerator.generatePassword()).thenReturn("securePass1");
        when(trainingTypeRepository.findByName("FITNESS")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any(Trainer.class))).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = service.create(input);

        assertNotNull(result);
        verify(trainerRepository).save(argThat(t ->
                "Arnold.Schwarzenegger".equals(t.getUsername()) &&
                        "securePass1".equals(t.getPassword()) &&
                        Boolean.TRUE.equals(t.getIsActive())
        ));
    }

    @Test
    void create_collectsUsernamesFromBothCollections() {
        Trainee existingTrainee = Trainee.builder().username("Existing.Trainee").build();

        TrainerDto input = TrainerDto.builder()
                .firstName("Chris").lastName("Bumstead").specialization("FITNESS").build();

        when(trainerRepository.findAll()).thenReturn(List.of());
        when(traineeRepository.findAll()).thenReturn(List.of(existingTrainee));
        when(profileGenerator.generateUsername(eq("Chris"), eq("Bumstead"), anyList()))
                .thenReturn("Chris.Bumstead1");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(trainingTypeRepository.findByName("FITNESS")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.save(any())).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        service.create(input);

        verify(profileGenerator).generateUsername(eq("Chris"), eq("Bumstead"),
                argThat(list -> list.contains("Existing.Trainee")));
    }

    @Test
    void create_unknownSpecialization_throwsEntityNotFoundException() {
        TrainerDto input = TrainerDto.builder()
                .firstName("A").lastName("B").specialization("UNKNOWN").build();

        when(trainerRepository.findAll()).thenReturn(List.of());
        when(traineeRepository.findAll()).thenReturn(List.of());
        when(profileGenerator.generateUsername(any(), any(), anyList())).thenReturn("A.B");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(trainingTypeRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.create(input));
    }

    @Test
    void matchCredentials_delegatesToDao() {
        when(trainerRepository.matchCredentials("Chris.Bumstead", "pass123456")).thenReturn(true);

        assertTrue(service.matchCredentials("Chris.Bumstead", "pass123456"));
    }

    @Test
    void findByUsername_existing_returnsDto() {
        when(trainerRepository.findByUsername("Chris.Bumstead")).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = service.findByUsername("Chris.Bumstead");

        assertEquals("Chris.Bumstead", result.getUsername());
    }

    @Test
    void findByUsername_notFound_throwsEntityNotFoundException() {
        when(trainerRepository.findByUsername("Unknown")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findByUsername("Unknown"));
    }

    @Test
    void changePassword_validCredentials_delegatesChange() {
        when(trainerRepository.matchCredentials("Chris.Bumstead", "pass123456")).thenReturn(true);

        service.changePassword("Chris.Bumstead", "pass123456", "newPass99");

        verify(trainerRepository).changePassword("Chris.Bumstead", "newPass99");
    }

    @Test
    void changePassword_invalidCredentials_throwsException() {
        when(trainerRepository.matchCredentials("Chris.Bumstead", "wrong")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> service.changePassword("Chris.Bumstead", "wrong", "newPass"));
        verify(trainerRepository, never()).changePassword(any(), any());
    }

    @Test
    void update_existingTrainer_updatesFields() {
        TrainerDto updateDto = TrainerDto.builder()
                .username("Chris.Bumstead").firstName("Christie").lastName("B")
                .isActive(false).specialization("FITNESS").build();

        when(trainerRepository.findByUsername("Chris.Bumstead")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepository.findByName("FITNESS")).thenReturn(Optional.of(trainingType));
        when(trainerRepository.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        service.update(updateDto);

        assertEquals("Christie", trainer.getFirstName());
        assertEquals("B", trainer.getLastName());
        assertFalse(trainer.getIsActive());
    }

    @Test
    void update_notFound_throwsEntityNotFoundException() {
        when(trainerRepository.findByUsername("Unknown")).thenReturn(Optional.empty());
        TrainerDto dto = TrainerDto.builder().username("Unknown").build();

        assertThrows(EntityNotFoundException.class, () -> service.update(dto));
    }

    @Test
    void setActive_delegatesToDao() {
        service.setActive("Chris.Bumstead", false);

        verify(trainerRepository).setActive("Chris.Bumstead", false);
    }

    @Test
    void findAll_returnsAllTrainers() {
        when(trainerRepository.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        List<TrainerDto> result = service.findAll();

        assertEquals(1, result.size());
        verify(trainerRepository).findAll();
    }
}
