package com.hitruk.service;

import com.hitruk.gym.crm.exception.EntityNotFoundException;
import com.hitruk.gym.crm.mapper.Mapper;
import com.hitruk.gym.crm.model.dao.Dao;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.service.TraineeServiceImpl;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    @Mock
    private Dao<Long, Trainee> traineeDao;

    @Mock
    private Dao<Long, Trainer> trainerDao;

    @Mock
    private Mapper<Trainee, TraineeDto> traineeMapper;

    @Mock
    private ProfileGenerator profileGenerator;

    @InjectMocks
    private TraineeServiceImpl service;

    private Trainee trainee;
    private TraineeDto traineeDto;

    @BeforeEach
    void setUp() {
        trainee = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("pass123456")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("New York")
                .build();

        traineeDto = TraineeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("pass123456")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("New York")
                .build();
    }

    @Test
    void findById_existingId_returnsDto() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John.Smith", result.getUsername());
        verify(traineeDao).findById(1L);
    }

    @Test
    void findById_nonExistingId_throwsEntityNotFoundException() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> service.findById(99L));
        verify(traineeDao).findById(99L);
    }

    @Test
    void create_setsUsernameAndPasswordAndActivatesEntity() {
        TraineeDto inputDto = TraineeDto.builder()
                .firstName("Jane")
                .lastName("Doe")
                .build();
        Trainee inputEntity = Trainee.builder().firstName("Jane").lastName("Doe").build();

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of());
        when(traineeMapper.toEntity(inputDto)).thenReturn(inputEntity);
        when(profileGenerator.generateUsername(eq("Jane"), eq("Doe"), anyCollection()))
                .thenReturn("Jane.Doe");
        when(profileGenerator.generatePassword()).thenReturn("abc1234xyz");
        when(traineeDao.create(inputEntity)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.create(inputDto);

        assertNotNull(result);
        assertEquals("Jane.Doe", inputEntity.getUsername());
        assertEquals("abc1234xyz", inputEntity.getPassword());
        assertTrue(inputEntity.getIsActive());
        verify(profileGenerator).generateUsername(eq("Jane"), eq("Doe"), anyCollection());
        verify(profileGenerator).generatePassword();
        verify(traineeDao).create(inputEntity);
    }

    @Test
    void create_combinesTraineesAndTrainersForUniquenessCheck() {
        Trainer existingTrainer = Trainer.builder().username("Jane.Doe").build();
        TraineeDto inputDto = TraineeDto.builder().firstName("Jane").lastName("Doe").build();
        Trainee inputEntity = Trainee.builder().firstName("Jane").lastName("Doe").build();

        when(traineeDao.findAll()).thenReturn(List.of());
        when(trainerDao.findAll()).thenReturn(List.of(existingTrainer));
        when(traineeMapper.toEntity(inputDto)).thenReturn(inputEntity);
        when(profileGenerator.generateUsername(eq("Jane"), eq("Doe"), anyCollection()))
                .thenReturn("Jane.Doe1");
        when(profileGenerator.generatePassword()).thenReturn("pass");
        when(traineeDao.create(inputEntity)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        service.create(inputDto);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Collection<? extends com.hitruk.gym.crm.model.entity.User>> captor =
                ArgumentCaptor.forClass(Collection.class);
        verify(profileGenerator).generateUsername(eq("Jane"), eq("Doe"), captor.capture());
        assertTrue(captor.getValue().contains(existingTrainer));
    }

    @Test
    void update_existingTrainee_returnsUpdatedDto() {
        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        TraineeDto result = service.update(traineeDto);

        assertNotNull(result);
        verify(traineeDao).update(trainee);
    }

    @Test
    void update_existingTrainee_updatesAllFields() {
        TraineeDto updateDto = TraineeDto.builder()
                .id(1L)
                .firstName("Updated")
                .lastName("Name")
                .username("Updated.Name")
                .password("newpass")
                .isActive(false)
                .dateOfBirth(LocalDate.of(1995, Month.MAY, 5))
                .address("Chicago")
                .build();

        when(traineeDao.findById(1L)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(trainee)).thenReturn(trainee);
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        service.update(updateDto);

        assertEquals("Updated", trainee.getFirstName());
        assertEquals("Name", trainee.getLastName());
        assertEquals("Updated.Name", trainee.getUsername());
        assertEquals("newpass", trainee.getPassword());
        assertFalse(trainee.getIsActive());
        assertEquals("Chicago", trainee.getAddress());
    }

    @Test
    void update_nonExistingTrainee_throwsEntityNotFoundException() {
        when(traineeDao.findById(99L)).thenReturn(Optional.empty());
        TraineeDto dto = TraineeDto.builder().id(99L).build();

        assertThrows(EntityNotFoundException.class, () -> service.update(dto));
        verify(traineeDao, never()).update(any());
    }

    @Test
    void delete_existingId_returnsTrue() {
        when(traineeDao.delete(1L)).thenReturn(true);

        assertTrue(service.delete(1L));
        verify(traineeDao).delete(1L);
    }

    @Test
    void delete_nonExistingId_returnsFalse() {
        when(traineeDao.delete(99L)).thenReturn(false);

        assertFalse(service.delete(99L));
    }

    @Test
    void findAll_returnsAllTrainees() {
        when(traineeDao.findAll()).thenReturn(List.of(trainee));
        when(traineeMapper.toDto(trainee)).thenReturn(traineeDto);

        List<TraineeDto> result = service.findAll();

        assertEquals(1, result.size());
        verify(traineeDao).findAll();
    }
}
