package com.hitruk.mapper;

import com.hitruk.gym.crm.mapper.TraineeMapper;
import com.hitruk.gym.crm.model.dto.TraineeDto;
import com.hitruk.gym.crm.model.entity.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private TraineeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TraineeMapper();
    }

    private Trainee buildTrainee() {
        return Trainee.builder()
                .id(1L).firstName("John").lastName("Smith")
                .username("John.Smith").password("pass123456").isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.MARCH, 15)).address("New York").build();
    }

    @Test
    void toDto_mapsAllUserFields() {
        Trainee entity = buildTrainee();

        TraineeDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals("John", dto.getFirstName());
        assertEquals("Smith", dto.getLastName());
        assertEquals("John.Smith", dto.getUsername());
        assertEquals("pass123456", dto.getPassword());
        assertTrue(dto.getIsActive());
    }

    @Test
    void toDto_mapsTraineeSpecificFields() {
        Trainee entity = buildTrainee();

        TraineeDto dto = mapper.toDto(entity);

        assertEquals(LocalDate.of(1990, Month.MARCH, 15), dto.getDateOfBirth());
        assertEquals("New York", dto.getAddress());
    }

    @Test
    void toDto_isActiveFalse_preservedCorrectly() {
        Trainee entity = Trainee.builder().firstName("Jane").lastName("Doe").isActive(false).build();

        TraineeDto dto = mapper.toDto(entity);

        assertFalse(dto.getIsActive());
    }

    @Test
    void toDto_nullDateOfBirth_mapsAsNull() {
        Trainee entity = Trainee.builder().firstName("John").lastName("Smith").dateOfBirth(null).build();

        TraineeDto dto = mapper.toDto(entity);

        assertNull(dto.getDateOfBirth());
    }

    @Test
    void toDto_emptyTrainersList_returnsEmptyList() {
        Trainee entity = buildTrainee();

        TraineeDto dto = mapper.toDto(entity);

        assertNotNull(dto.getTrainerUsernames());
        assertTrue(dto.getTrainerUsernames().isEmpty());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Trainee entity = buildTrainee();

        TraineeDto dto = mapper.toDto(entity);

        entity.setFirstName("Changed");
        assertEquals("John", dto.getFirstName());
    }

    @Test
    void toEntity_mapsUserFields() {
        TraineeDto dto = TraineeDto.builder()
                .id(1L).firstName("John").lastName("Smith")
                .username("John.Smith").password("pass123456").isActive(true)
                .dateOfBirth(LocalDate.of(1990, Month.MARCH, 15)).address("New York").build();

        Trainee entity = mapper.toEntity(dto);

        assertEquals("John", entity.getFirstName());
        assertEquals("Smith", entity.getLastName());
        assertEquals("John.Smith", entity.getUsername());
    }

    @Test
    void toEntity_mapsTraineeSpecificFields() {
        TraineeDto dto = TraineeDto.builder()
                .firstName("John").lastName("Smith").id(5L)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1)).address("Chicago").build();

        Trainee entity = mapper.toEntity(dto);

        assertEquals(5L, entity.getId());
        assertEquals(LocalDate.of(1990, Month.JANUARY, 1), entity.getDateOfBirth());
        assertEquals("Chicago", entity.getAddress());
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TraineeDto dto = TraineeDto.builder().firstName("John").lastName("Smith").build();

        Trainee entity = mapper.toEntity(dto);

        assertNull(entity.getId());
    }

    @Test
    void roundTrip_preservesCoreFields() {
        Trainee original = buildTrainee();

        TraineeDto dto = mapper.toDto(original);
        Trainee restored = mapper.toEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getFirstName(), restored.getFirstName());
        assertEquals(original.getUsername(), restored.getUsername());
    }
}