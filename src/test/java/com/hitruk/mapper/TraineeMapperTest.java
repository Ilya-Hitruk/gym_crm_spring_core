package com.hitruk.mapper;

import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {
    private TraineeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeMapper();
    }

    @Test
    void toDto_mapsAllFields() {
        Trainee entity = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("pass123456")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 3, 15))
                .address("New York")
                .build();

        TraineeDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("John.Smith", dto.getUsername());
        assertEquals("pass123456", dto.getPassword());
        assertTrue(dto.getIsActive());
        assertEquals(LocalDate.of(1990, 3, 15), dto.getDateOfBirth());
        assertEquals("New York", dto.getAddress());
    }

    @Test
    void toDto_isActiveFalse_preservedCorrectly() {
        Trainee entity = Trainee.builder()
                .id(2L)
                .firstName("Jane")
                .lastName("Doe")
                .isActive(false)
                .build();

        TraineeDto dto = mapper.toDto(entity);

        assertFalse(dto.getIsActive());
    }

    @Test
    void toDto_nullDateOfBirth_mapsAsNull() {
        Trainee entity = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(null)
                .build();

        TraineeDto dto = mapper.toDto(entity);

        assertNull(dto.getDateOfBirth());
    }

    @Test
    void toDto_nullAddress_mapsAsNull() {
        Trainee entity = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .address(null)
                .build();

        TraineeDto dto = mapper.toDto(entity);

        assertNull(dto.getAddress());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Trainee entity = Trainee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .build();

        TraineeDto dto = mapper.toDto(entity);

        entity.setFirstName("Changed");
        assertEquals("John", dto.getFirstName());
    }

    @Test
    void toEntity_mapsAllFields() {
        TraineeDto dto = TraineeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .username("John.Smith")
                .password("pass123456")
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 3, 15))
                .address("New York")
                .build();

        Trainee entity = mapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals("John", entity.getFirstName());
        assertEquals("Smith", entity.getLastName());
        assertEquals("John.Smith", entity.getUsername());
        assertEquals("pass123456", entity.getPassword());
        assertTrue(entity.getIsActive());
    }

    @Test
    void toEntity_doesNotMapDateOfBirthAndAddress() {
        TraineeDto dto = TraineeDto.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("New York")
                .build();

        Trainee entity = mapper.toEntity(dto);
        assertNull(entity.getDateOfBirth());
        assertNull(entity.getAddress());
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TraineeDto dto = TraineeDto.builder()
                .firstName("John")
                .lastName("Smith")
                .build();

        Trainee entity = mapper.toEntity(dto);

        assertNull(entity.getId());
    }

    @Test
    void toDto_thenToEntity_preservesCoreFields() {
        Trainee original = Trainee.builder()
                .id(5L)
                .firstName("Alice")
                .lastName("Walker")
                .username("Alice.Walker")
                .password("xyz9876543")
                .isActive(true)
                .build();

        TraineeDto dto = mapper.toDto(original);
        Trainee restored = mapper.toEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getFirstName(), restored.getFirstName());
        assertEquals(original.getLastName(), restored.getLastName());
        assertEquals(original.getUsername(), restored.getUsername());
        assertEquals(original.getPassword(), restored.getPassword());
        assertEquals(original.getIsActive(), restored.getIsActive());
    }
}
