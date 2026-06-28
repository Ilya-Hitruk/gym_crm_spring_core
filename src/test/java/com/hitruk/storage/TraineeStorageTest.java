package com.hitruk.storage;

import com.hitruk.gym.crm.model.entity.Trainee;

import com.hitruk.gym.crm.storage.IdGenerator;
import com.hitruk.gym.crm.storage.TraineeStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraineeStorageTest {
    private TraineeStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TraineeStorage();
        storage.setIdGenerator(new IdGenerator());
    }

    private Trainee trainee(String firstName, String lastName) {
        return Trainee.builder()
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(LocalDate.of(1990, Month.JANUARY, 1))
                .address("New York")
                .build();
    }

    @Test
    void create_assignsSequentialIds() {
        Trainee t1 = storage.create(trainee("John", "Smith"));
        Trainee t2 = storage.create(trainee("Jane", "Doe"));
        Trainee t3 = storage.create(trainee("Bob", "Brown"));

        assertEquals(1L, t1.getId());
        assertEquals(2L, t2.getId());
        assertEquals(3L, t3.getId());
    }

    @Test
    void create_returnsSameEntityReference() {
        Trainee input = trainee("John", "Smith");
        Trainee result = storage.create(input);

        assertSame(input, result);
    }

    @Test
    void create_doesNotOverwriteExistingUsername() {
        Trainee t = trainee("John", "Smith");
        t.setUsername("John.Smith");
        t.setPassword("pass");

        storage.create(t);

        assertEquals("John.Smith", t.getUsername());
        assertEquals("pass", t.getPassword());
    }

    @Test
    void findById_afterCreate_returnsEntity() {
        Trainee created = storage.create(trainee("John", "Smith"));

        Trainee found = storage.findById(created.getId());

        assertNotNull(found);
        assertEquals("John", found.getFirstName());
        assertEquals("Smith", found.getLastName());
    }

    @Test
    void findById_nonExistingId_returnsNull() {
        assertNull(storage.findById(999L));
    }

    @Test
    void findById_afterDelete_returnsNull() {
        Trainee created = storage.create(trainee("John", "Smith"));
        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
    }

    @Test
    void findAll_emptyStorage_returnsEmptyList() {
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void findAll_returnsAllStoredEntities() {
        storage.create(trainee("John", "Smith"));
        storage.create(trainee("Jane", "Doe"));
        storage.create(trainee("Bob", "Brown"));

        assertEquals(3, storage.findAll().size());
    }

    @Test
    void findAll_returnsImmutableList() {
        storage.create(trainee("John", "Smith"));

        List<Trainee> list = storage.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> list.add(trainee("Hacker", "Attack")));
    }

    @Test
    void findAll_afterDelete_doesNotContainDeletedEntity() {
        Trainee t1 = storage.create(trainee("John", "Smith"));
        Trainee t2 = storage.create(trainee("Jane", "Doe"));

        storage.delete(t1.getId());

        List<Trainee> all = storage.findAll();
        assertEquals(1, all.size());
        assertEquals(t2.getId(), all.getFirst().getId());
    }

    @Test
    void update_replacesEntityUnderSameId() {
        Trainee original = storage.create(trainee("John", "Smith"));
        Trainee replacement = trainee("Updated", "Name");

        storage.update(original.getId(), replacement);

        Trainee found = storage.findById(original.getId());
        assertEquals("Updated", found.getFirstName());
        assertEquals("Name", found.getLastName());
    }

    @Test
    void update_returnsProvidedEntity() {
        Trainee created = storage.create(trainee("John", "Smith"));
        Trainee replacement = trainee("Updated", "Name");

        Trainee result = storage.update(created.getId(), replacement);

        assertSame(replacement, result);
    }

    @Test
    void update_withNewId_storesUnderGivenKey() {
        storage.create(trainee("John", "Smith"));
        Trainee directInsert = trainee("Direct", "Insert");

        storage.update(42L, directInsert);

        assertNotNull(storage.findById(42L));
        assertEquals("Direct", storage.findById(42L).getFirstName());
    }

    @Test
    void delete_existingEntity_returnsTrue() {
        Trainee created = storage.create(trainee("John", "Smith"));

        assertTrue(storage.delete(created.getId()));
    }

    @Test
    void delete_nonExistingId_returnsFalse() {
        assertFalse(storage.delete(999L));
    }

    @Test
    void delete_removesEntityFromStorage() {
        Trainee created = storage.create(trainee("John", "Smith"));

        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
        assertEquals(0, storage.findAll().size());
    }

    @Test
    void delete_calledTwice_secondCallReturnsFalse() {
        Trainee created = storage.create(trainee("John", "Smith"));

        storage.delete(created.getId());
        boolean secondDelete = storage.delete(created.getId());

        assertFalse(secondDelete);
    }
}
