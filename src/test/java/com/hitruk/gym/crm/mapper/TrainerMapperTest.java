package com.hitruk.gym.crm.mapper;

import com.hitruk.gym.crm.api.dto.TrainerDto;
import com.hitruk.gym.crm.api.dto.UserCredentials;
import com.hitruk.gym.crm.api.dto.response.TrainerSummary;
import com.hitruk.gym.crm.entity.Trainer;
import com.hitruk.gym.crm.entity.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private TrainerMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TrainerMapper();
    }

    private Trainer buildTrainer(String typeName) {
        TrainingType type = TrainingType.builder().id(1L).name(typeName).build();
        return Trainer.builder()
                .id(1L).firstName("Chris").lastName("Bumstead")
                .username("Chris.Bumstead").password("pass123456").isActive(true)
                .specialization(type).build();
    }

    @Test
    void toDto_mapsAllUserFields() {
        Trainer entity = buildTrainer("FITNESS");

        TrainerDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.getId());
        assertEquals("Chris", dto.getFirstName());
        assertEquals("Bumstead", dto.getLastName());
        assertEquals("Chris.Bumstead", dto.getCredentials().getUsername());
        assertEquals("pass123456", dto.getCredentials().getPassword());
        assertTrue(dto.getIsActive());
    }

    @Test
    void toDto_mapsSpecializationName() {
        TrainerDto dto = mapper.toDto(buildTrainer("YOGA"));

        assertEquals("YOGA", dto.getSpecialization());
    }

    @Test
    void toDto_isActiveFalse_preservedCorrectly() {
        TrainingType type = TrainingType.builder().name("FITNESS").build();
        Trainer entity = Trainer.builder().firstName("Chris").lastName("Bumstead")
                .isActive(false).specialization(type).build();

        assertFalse(mapper.toDto(entity).getIsActive());
    }

    @Test
    void toDto_doesNotShareReferenceWithEntity() {
        Trainer entity = buildTrainer("FITNESS");

        TrainerDto dto = mapper.toDto(entity);

        entity.setFirstName("Changed");
        assertEquals("Chris", dto.getFirstName());
    }

    @Test
    void toEntity_mapsUserFields() {
        TrainerDto dto = TrainerDto.builder()
                .id(1L).firstName("Chris").lastName("Bumstead")
                .credentials(UserCredentials.of("Chris.Bumstead", "pass123456"))
                .isActive(true).specialization("FITNESS").build();

        Trainer entity = mapper.toEntity(dto);

        assertEquals("Chris", entity.getFirstName());
        assertEquals("Chris.Bumstead", entity.getUsername());
        assertEquals("pass123456", entity.getPassword());
    }

    @Test
    void toEntity_mapsSpecializationToTrainingType() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Arnold").lastName("S").specialization("STRENGTH").build();

        Trainer entity = mapper.toEntity(dto);

        assertNotNull(entity.getSpecialization());
        assertEquals("STRENGTH", entity.getSpecialization().getName());
    }

    @Test
    void toEntity_nullId_mapsAsNull() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Chris").lastName("Bumstead").specialization("FITNESS").build();

        assertNull(mapper.toEntity(dto).getId());
    }

    @Test
    void toEntity_nullSpecialization_mapsAsNull() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Chris").lastName("Bumstead").build();

        assertNull(mapper.toEntity(dto).getSpecialization());
    }

    @Test
    void toEntity_nullCredentials_mapsUsernameAndPasswordAsNull() {
        TrainerDto dto = TrainerDto.builder()
                .firstName("Chris").lastName("Bumstead").build();

        Trainer entity = mapper.toEntity(dto);

        assertNull(entity.getUsername());
        assertNull(entity.getPassword());
    }

    @Test
    void toSummary_mapsUsernameFirstNameLastNameSpecialization() {
        Trainer entity = buildTrainer("FITNESS");

        TrainerSummary summary = mapper.toSummary(entity);

        assertEquals("Chris.Bumstead", summary.getUsername());
        assertEquals("Chris", summary.getFirstName());
        assertEquals("Bumstead", summary.getLastName());
        assertEquals("FITNESS", summary.getSpecialization());
    }

    @Test
    void toSummary_nullSpecialization_mapsAsNull() {
        Trainer entity = Trainer.builder().username("Chris.Bumstead").firstName("Chris").lastName("Bumstead").build();

        TrainerSummary summary = mapper.toSummary(entity);

        assertNull(summary.getSpecialization());
    }

    @Test
    void roundTrip_preservesCoreFields() {
        Trainer original = buildTrainer("FITNESS");

        TrainerDto dto = mapper.toDto(original);
        Trainer restored = mapper.toEntity(dto);

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getFirstName(), restored.getFirstName());
        assertEquals(original.getUsername(), restored.getUsername());
        assertEquals(original.getSpecialization().getName(), restored.getSpecialization().getName());
    }
}
