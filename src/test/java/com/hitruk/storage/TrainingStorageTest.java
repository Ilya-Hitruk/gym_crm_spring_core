package com.hitruk.storage;

import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.model.entity.TrainingType;
import com.hitruk.gym.crm.storage.IdGenerator;
import com.hitruk.gym.crm.storage.TrainingStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingStorageTest {
    private TrainingStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TrainingStorage();
        storage.setIdGenerator(new IdGenerator());
    }

    private Training training(String name, TrainingType type) {
        return Training.builder()
                .traineeId(1L)
                .trainerId(1L)
                .name(name)
                .type(type)
                .date(LocalDateTime.of(2026, Month.JULY, 1, 10, 0))
                .duration(Duration.ofHours(1))
                .build();
    }

    @Test
    void create_assignsSequentialIds() {
        Training t1 = storage.create(training("Chest Day", TrainingType.STRENGTH));
        Training t2 = storage.create(training("Cardio", TrainingType.CARDIO));
        Training t3 = storage.create(training("Yoga", TrainingType.YOGA));

        assertEquals(1L, t1.getId());
        assertEquals(2L, t2.getId());
        assertEquals(3L, t3.getId());
    }

    @Test
    void create_returnsSameEntityReference() {
        Training input = training("Session", TrainingType.CARDIO);

        assertSame(input, storage.create(input));
    }

    @Test
    void create_preservesAllFields() {
        LocalDateTime date = LocalDateTime.of(2026, Month.AUGUST, 15, 9, 30);
        Duration duration = Duration.ofMinutes(90);

        Training input = Training.builder()
                .traineeId(5L)
                .trainerId(3L)
                .name("Morning yoga")
                .type(TrainingType.YOGA)
                .date(date)
                .duration(duration)
                .build();

        Training created = storage.create(input);

        assertEquals(5L, created.getTraineeId());
        assertEquals(3L, created.getTrainerId());
        assertEquals("Morning yoga", created.getName());
        assertEquals(TrainingType.YOGA, created.getType());
        assertEquals(date, created.getDate());
        assertEquals(duration, created.getDuration());
    }

    @Test
    void findById_afterCreate_returnsEntity() {
        Training created = storage.create(training("Chest Day", TrainingType.STRENGTH));

        Training found = storage.findById(created.getId());

        assertNotNull(found);
        assertEquals("Chest Day", found.getName());
        assertEquals(TrainingType.STRENGTH, found.getType());
    }

    @Test
    void findById_nonExistingId_returnsNull() {
        assertNull(storage.findById(999L));
    }

    @Test
    void findById_afterDelete_returnsNull() {
        Training created = storage.create(training("Session", TrainingType.CARDIO));
        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
    }

    @Test
    void findAll_emptyStorage_returnsEmptyList() {
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void findAll_returnsAllStoredEntities() {
        storage.create(training("A", TrainingType.STRENGTH));
        storage.create(training("B", TrainingType.CARDIO));
        storage.create(training("C", TrainingType.YOGA));

        assertEquals(3, storage.findAll().size());
    }

    @Test
    void findAll_returnsImmutableList() {
        storage.create(training("Session", TrainingType.CARDIO));

        List<Training> list = storage.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> list.add(training("Extra", TrainingType.FITNESS)));
    }

    @Test
    void findAll_afterDelete_doesNotContainDeletedEntity() {
        Training t1 = storage.create(training("A", TrainingType.STRENGTH));
        storage.create(training("B", TrainingType.CARDIO));

        storage.delete(t1.getId());

        List<Training> all = storage.findAll();
        assertEquals(1, all.size());
        assertEquals("B", all.getFirst().getName());
    }

    @Test
    void update_replacesEntityAndSetsId() {
        Training original = storage.create(training("Old", TrainingType.STRENGTH));
        Training replacement = training("New", TrainingType.YOGA);

        storage.update(original.getId(), replacement);

        Training found = storage.findById(original.getId());
        assertEquals("New", found.getName());
        assertEquals(TrainingType.YOGA, found.getType());
        assertEquals(original.getId(), found.getId());
    }

    @Test
    void update_returnsProvidedEntity() {
        Training created = storage.create(training("Old", TrainingType.STRENGTH));
        Training replacement = training("New", TrainingType.CARDIO);

        Training result = storage.update(created.getId(), replacement);

        assertSame(replacement, result);
    }

    @Test
    void update_withArbitraryKey_storesUnderThatKey() {
        Training directInsert = training("Direct", TrainingType.FITNESS);

        storage.update(100L, directInsert);

        assertNotNull(storage.findById(100L));
        assertEquals("Direct", storage.findById(100L).getName());
        assertEquals(100L, storage.findById(100L).getId());
    }

    @Test
    void delete_existingEntity_returnsTrue() {
        Training created = storage.create(training("Session", TrainingType.CARDIO));

        assertTrue(storage.delete(created.getId()));
    }

    @Test
    void delete_nonExistingId_returnsFalse() {
        assertFalse(storage.delete(999L));
    }

    @Test
    void delete_removesEntityFromStorage() {
        Training created = storage.create(training("Session", TrainingType.CARDIO));

        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void delete_calledTwice_secondCallReturnsFalse() {
        Training created = storage.create(training("Session", TrainingType.CARDIO));

        storage.delete(created.getId());

        assertFalse(storage.delete(created.getId()));
    }

    @Test
    void multipleEntities_independentLifecycles() {
        Training t1 = storage.create(training("A", TrainingType.STRENGTH));
        Training t2 = storage.create(training("B", TrainingType.CARDIO));
        Training t3 = storage.create(training("C", TrainingType.YOGA));

        storage.delete(t2.getId());

        assertNotNull(storage.findById(t1.getId()));
        assertNull(storage.findById(t2.getId()));
        assertNotNull(storage.findById(t3.getId()));
        assertEquals(2, storage.findAll().size());
    }
}
