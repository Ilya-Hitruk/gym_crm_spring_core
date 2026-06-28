package com.hitruk.mapper;

import com.hitruk.gym.crm.mapper.TrainerMapper;
import com.hitruk.gym.crm.model.dto.TrainerDto;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {
    private TrainerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerMapper();
    }

    @Test
    void toDto_mapsAllFields() {
        Trainer entity = Trainer.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .username("Chris.Bumstead")
                .password("pass123456")
                .isActive(true)
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        TrainerDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals("Chris", dto.getFirstName());
        assertEquals("Bumstead", dto.getLastName());
        assertEquals("Chris.Bumstead", dto.getUsername());
        assertEquals("pass123456", dto.getPassword());
        assertTrue(dto.getIsActive());
        assertEquals("BODYBUILDING", dto.getSpecialization());
    }

    @Test
    void toDto_specializationCrossfit_mapsToString() {
        Trainer entity = Trainer.builder()
                .id(2L)
                .firstName("Mat")
                .lastName("Fraser")
                .specialization(TrainerSpecialization.CROSSFIT)
                .build();

        TrainerDto dto = mapper.toDto(entity);

        assertEquals("CROSSFIT", dto.getSpecialization());
    }

    @Test
    void toDto_specializationFitness_mapsToString() {
        Trainer entity = Trainer.builder()
                .id(3L)
                .firstName("Jeff")
                .lastName("Nippard")
                .specialization(TrainerSpecialization.FITNESS)
                .build();

        assertEquals("FITNESS", mapper.toDto(entity).getSpecialization());
    }

    @Test
    void toDto_isActiveFalse_preservedCorrectly() {
        Trainer entity = Trainer.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .isActive(false)
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        assertFalse(mapper.toDto(entity).getIsActive());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Trainer entity = Trainer.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        TrainerDto dto = mapper.toDto(entity);

        entity.setFirstName("Changed");
        assertEquals("Chris", dto.getFirstName());
    }

    @Test
    void toEntity_mapsAllFields() {
        TrainerDto dto = TrainerDto.builder()
                .id(1L)
                .firstName("Chris")
                .lastName("Bumstead")
                .username("Chris.Bumstead")
                .password("pass123456")
                .isActive(true)
                .specialization("BODYBUILDING")
                .build();

        Trainer entity = mapper.toEntity(dto);

        assertEquals(1L, entity.getId());
        assertEquals("Chris", entity.getFirstName());
        assertEquals("Bumstead", entity.getLastName());
        assertEquals("Chris.Bumstead", entity.getUsername());
        assertEquals("pass123456", entity.getPassword());
        assertTrue(entity.getIsActive());
        assertEquals(TrainerSpecialization.BODYBUILDING, entity.getSpecialization());
    }

    @Test
    void toEntity_specializationCrossfit_parsedCorrectly() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Mat")
                .lastName("Fraser")
                .specialization("CROSSFIT")
                .build();

        assertEquals(TrainerSpecialization.CROSSFIT, mapper.toEntity(dto).getSpecialization());
    }

    @Test
    void toEntity_specializationFitness_parsedCorrectly() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Jeff")
                .lastName("Nippard")
                .specialization("FITNESS")
                .build();

        assertEquals(TrainerSpecialization.FITNESS, mapper.toEntity(dto).getSpecialization());
    }

    @Test
    void toEntity_invalidSpecialization_throwsIllegalArgumentException() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("X")
                .lastName("Y")
                .specialization("UNKNOWN_SPEC")
                .build();

        assertThrows(IllegalArgumentException.class, () -> mapper.toEntity(dto));
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Chris")
                .lastName("Bumstead")
                .specialization("BODYBUILDING")
                .build();

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void toDto_thenToEntity_preservesAllFields() {
        Trainer original = Trainer.builder()
                .id(7L)
                .firstName("Arnold")
                .lastName("Schwarzenegger")
                .username("Arnold.Schwarzenegger")
                .password("terminator1")
                .isActive(true)
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        TrainerDto dto = mapper.toDto(original);
        Trainer restored = mapper.toEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getFirstName(), restored.getFirstName());
        assertEquals(original.getLastName(), restored.getLastName());
        assertEquals(original.getUsername(), restored.getUsername());
        assertEquals(original.getPassword(), restored.getPassword());
        assertEquals(original.getIsActive(), restored.getIsActive());
        assertEquals(original.getSpecialization(), restored.getSpecialization());
    }
}
