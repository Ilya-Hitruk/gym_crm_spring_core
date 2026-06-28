package com.hitruk.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import com.hitruk.gym.crm.service.TrainerServiceImpl;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {

    @Mock
    private Dao<Long, Trainer> trainerDao;

    @Mock
    private Dao<Long, Trainee> traineeDao;

    @Mock
    private Mapper<Trainer, TrainerDto> trainerMapper;

    @Mock
    private ProfileGenerator profileGenerator;

    @InjectMocks
    private TrainerServiceImpl service;

    private Trainer trainer;
    private TrainerDto trainerDto;

    @BeforeEach
    void setUp() {
        trainer = Trainer.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .username("Chris.Bumstead")
                .password("pass123456")
                .isActive(true)
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        trainerDto = TrainerDto.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .username("Chris.Bumstead")
                .password("pass123456")
                .isActive(true)
                .specialization("BODYBUILDING")
                .build();
    }

    @Test
    void findById_existingId_returnsDto() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals("Chris.Bumstead", result.getUsername());
        verify(trainerDao).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(trainerDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
    }

    @Test
    void create_setsUsernameAndPasswordAndActivatesEntity() {
        TrainerDto inputDto = TrainerDto.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .specialization("BODYBUILDING")
                .build();
        Trainer inputEntity = Trainer.builder()
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerMapper.toEntity(inputDto)).thenReturn(inputEntity);
        when(profileGenerator.generateUsername(eq("Arnold"), eq("Schwarzenegger"), anyCollection()))
                .thenReturn("Arnold.Schwarzenegger");
        when(profileGenerator.generatePassword()).thenReturn("securePass1");
        when(trainerDao.create(inputEntity)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = service.create(inputDto);

        assertNotNull(result);
        assertEquals("Arnold.Schwarzenegger", inputEntity.getUsername());
        assertEquals("securePass1", inputEntity.getPassword());
        assertTrue(inputEntity.getIsActive());
        verify(profileGenerator).generateUsername(eq("Arnold"), eq("Schwarzenegger"), anyCollection());
        verify(profileGenerator).generatePassword();
        verify(trainerDao).create(inputEntity);
    }

    @Test
    void create_combinesTrainersAndTraineesForUniquenessCheck() {
        Trainee existingTrainee = Trainee.builder().username("Chris.Bumstead").build();
        TrainerDto inputDto = TrainerDto.builder()
                .firstName("Chris").lastName("Bumstead").specialization("BODYBUILDING").build();
        Trainer inputEntity = Trainer.builder()
                .firstName("Chris").lastName("Bumstead").specialization(TrainerSpecialization.BODYBUILDING).build();

        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeDao.findAll()).thenReturn(List.of(existingTrainee));
        when(trainerMapper.toEntity(inputDto)).thenReturn(inputEntity);
        when(profileGenerator.generateUsername(eq("Chris"), eq("Bumstead"), anyCollection()))
                .thenReturn("Chris.Bumstead1");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(trainerDao.create(inputEntity)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        service.create(inputDto);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<? extends com.hitruk.gym.crm.model.entity.User>> captor =
                ArgumentCaptor.forClass(Collection.class);
        verify(profileGenerator).generateUsername(eq("Chris"), eq("Bumstead"), captor.capture());
        assertTrue(captor.getValue().contains(existingTrainee));
    }

    @Test
    void update_existingTrainer_returnsUpdatedDto() {
        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        TrainerDto result = service.update(trainerDto);

        assertNotNull(result);
        verify(trainerDao).update(trainer);
    }

    @Test
    void update_existingTrainer_updatesAllFields() {
        TrainerDto updateDto = TrainerDto.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Trainer")
                .username("Updated.Trainer")
                .password("newpass")
                .isActive(false)
                .specialization("FITNESS")
                .build();

        when(trainerDao.findById(1L)).thenReturn(Optional.of(trainer));
        when(trainerDao.update(trainer)).thenReturn(trainer);
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        service.update(updateDto);

        assertEquals("Updated", trainer.getFirstName());
        assertEquals("Trainer", trainer.getLastName());
        assertEquals("Updated.Trainer", trainer.getUsername());
        assertEquals("newpass", trainer.getPassword());
        assertFalse(trainer.getIsActive());
        assertEquals(TrainerSpecialization.FITNESS, trainer.getSpecialization());
    }

    @Test
    void update_nonExistingTrainer_throwsEntityNotFoundException() {
        when(trainerDao.findById(99L)).thenReturn(Optional.empty());
        TrainerDto dto = TrainerDto.builder().id(99L).specialization("FITNESS").build();

        assertThrows(EntityNotFoundException.class, () -> service.update(dto));
        verify(trainerDao, never()).update(any());
    }

    @Test
    void findAll_returnsAllTrainers() {
        when(trainerDao.findAll()).thenReturn(List.of(trainer));
        when(trainerMapper.toDto(trainer)).thenReturn(trainerDto);

        List<TrainerDto> result = service.findAll();

        assertEquals(1, result.size());
        verify(trainerDao).findAll();
    }
}
