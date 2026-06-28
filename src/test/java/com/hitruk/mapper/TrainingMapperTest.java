package com.hitruk.mapper;

import com.hitruk.gym.crm.mapper.TrainingMapper;
import com.hitruk.gym.crm.model.dto.TrainingDto;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {
    private TrainingMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainingMapper();
    }

    @Test
    void toDto_mapsAllFields() {
        LocalDateTime date = LocalDateTime.of(2026, 6, 25, 10, 0);
        Duration duration = Duration.ofMinutes(90);

        Training entity = Training.builder()
                .id(1L)
                .traineeId(2L)
                .trainerId(3L)
                .name("Chest workout")
                .type(TrainingType.STRENGTH)
                .date(date)
                .duration(duration)
                .build();

        TrainingDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getTraineeId());
        assertEquals(3L, dto.getTrainerId());
        assertEquals("Chest workout", dto.getName());
        assertEquals("STRENGTH", dto.getTrainingType());
        assertEquals(date, dto.getDate());
        assertEquals(duration, dto.getDuration());
    }

    @Test
    void toDto_trainingTypeCardio_mapsToString() {
        Training entity = Training.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Run")
                .type(TrainingType.CARDIO)
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(30))
                .build();

        assertEquals("CARDIO", mapper.toDto(entity).getTrainingType());
    }

    @Test
    void toDto_trainingTypeYoga_mapsToString() {
        Training entity = Training.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Yoga")
                .type(TrainingType.YOGA)
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(60))
                .build();

        assertEquals("YOGA", mapper.toDto(entity).getTrainingType());
    }

    @Test
    void toDto_trainingTypeFitness_mapsToString() {
        Training entity = Training.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Fit")
                .type(TrainingType.FITNESS)
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(45))
                .build();

        assertEquals("FITNESS", mapper.toDto(entity).getTrainingType());
    }

    @Test
    void toDto_durationPreservedExactly() {
        Duration duration = Duration.ofHours(2).plusMinutes(15);
        Training entity = Training.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Long session")
                .type(TrainingType.STRENGTH)
                .date(LocalDateTime.now()).duration(duration)
                .build();

        assertEquals(duration, mapper.toDto(entity).getDuration());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Training entity = Training.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Original")
                .type(TrainingType.CARDIO)
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(30))
                .build();

        TrainingDto dto = mapper.toDto(entity);

        entity.setName("Changed");
        assertEquals("Original", dto.getName());
    }

    @Test
    void toEntity_mapsAllFields() {
        LocalDateTime date = LocalDateTime.of(2026, 7, 1, 14, 0);
        Duration duration = Duration.ofMinutes(45);

        TrainingDto dto = TrainingDto.builder()
                .id(1L)
                .traineeId(2L)
                .trainerId(3L)
                .name("Crossfit session")
                .trainingType("CARDIO")
                .date(date)
                .duration(duration)
                .build();

        Training entity = mapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals(2L, entity.getTraineeId());
        assertEquals(3L, entity.getTrainerId());
        assertEquals("Crossfit session", entity.getName());
        assertEquals(TrainingType.CARDIO, entity.getType());
        assertEquals(date, entity.getDate());
        assertEquals(duration, entity.getDuration());
    }

    @Test
    void toEntity_trainingTypeStrength_parsedCorrectly() {
        TrainingDto dto = TrainingDto.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Lift")
                .trainingType("STRENGTH")
                .date(LocalDateTime.now()).duration(Duration.ofHours(1))
                .build();

        assertEquals(TrainingType.STRENGTH, mapper.toEntity(dto).getType());
    }

    @Test
    void toEntity_trainingTypeYoga_parsedCorrectly() {
        TrainingDto dto = TrainingDto.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("Stretch")
                .trainingType("YOGA")
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(50))
                .build();

        assertEquals(TrainingType.YOGA, mapper.toEntity(dto).getType());
    }

    @Test
    void toEntity_invalidTrainingType_throwsIllegalArgumentException() {
        TrainingDto dto = TrainingDto.builder()
                .id(1L).traineeId(1L).trainerId(1L).name("X")
                .trainingType("DANCING")
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(30))
                .build();

        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(dto));
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TrainingDto dto = TrainingDto.builder()
                .traineeId(1L).trainerId(1L).name("Session")
                .trainingType("FITNESS")
                .date(LocalDateTime.now()).duration(Duration.ofMinutes(30))
                .build();

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void toDto_thenToEntity_preservesAllFields() {
        LocalDateTime date = LocalDateTime.of(2026, 9, 10, 8, 0);
        Duration duration = Duration.ofHours(1).plusMinutes(30);

        Training original = Training.builder()
                .id(5L)
                .traineeId(3L)
                .trainerId(7L)
                .name("Morning yoga")
                .type(TrainingType.YOGA)
                .date(date)
                .duration(duration)
                .build();

        TrainingDto dto = mapper.toDto(original);
        Training restored = mapper.toEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getTraineeId(), restored.getTraineeId());
        assertEquals(original.getTrainerId(), restored.getTrainerId());
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getType(), restored.getType());
        assertEquals(original.getDate(), restored.getDate());
        assertEquals(original.getDuration(), restored.getDuration());
    }
}
