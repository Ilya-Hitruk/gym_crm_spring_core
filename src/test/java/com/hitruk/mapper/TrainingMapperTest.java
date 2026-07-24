package com.hitruk.mapper;

import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private TrainingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainingMapper();
    }

    private Training buildTraining() {
        TrainingType type = TrainingType.builder().id(1L).name("FITNESS").build();
        Trainee trainee = Trainee.builder().id(1L).username("John.Smith").build();
        Trainer trainer = Trainer.builder().id(1L).username("Chris.Bumstead").specialization(type).build();
        return Training.builder()
                .id(1L).trainee(trainee).trainer(trainer)
                .name("Morning Lift").type(type)
                .date(LocalDate.of(2024, Month.JANUARY, 15)).duration(60).build();
    }

    @Test
    void toDto_mapsAllFields() {
        Training entity = buildTraining();

        TrainingDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals("John.Smith", dto.getTraineeUsername());
        assertEquals("Chris.Bumstead", dto.getTrainerUsername());
        assertEquals("Morning Lift", dto.getName());
        assertEquals("FITNESS", dto.getTrainingType());
        assertEquals(LocalDate.of(2024, Month.JANUARY, 15), dto.getDate());
        assertEquals(60, dto.getDuration());
    }

    @Test
    void toDto_mapsTraineeAndTrainerUsernames() {
        Training entity = buildTraining();

        TrainingDto dto = mapper.toDto(entity);

        assertEquals("John.Smith", dto.getTraineeUsername());
        assertEquals("Chris.Bumstead", dto.getTrainerUsername());
    }

    @Test
    void toDto_mapsTrainingTypeName() {
        Training entity = buildTraining();

        assertEquals("FITNESS", mapper.toDto(entity).getTrainingType());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Training entity = buildTraining();

        TrainingDto dto = mapper.toDto(entity);

        entity.setName("Changed");
        assertEquals("Morning Lift", dto.getName());
    }

    @Test
    void toEntity_mapsNameDateDuration() {
        TrainingDto dto = TrainingDto.builder()
                .id(1L).name("Crossfit session")
                .date(LocalDate.of(2024, Month.MARCH, 20)).duration(45).build();

        Training entity = mapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals("Crossfit session", entity.getName());
        assertEquals(LocalDate.of(2024, Month.MARCH, 20), entity.getDate());
        assertEquals(45, entity.getDuration());
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TrainingDto dto = TrainingDto.builder().name("Session").build();

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void toEntity_doesNotSetTraineeOrTrainer() {
        TrainingDto dto = TrainingDto.builder()
                .traineeUsername("John.Smith").trainerUsername("Chris.Bumstead").build();

        Training entity = mapper.toEntity(dto);

        assertNull(entity.getTrainee());
        assertNull(entity.getTrainer());
    }
}
