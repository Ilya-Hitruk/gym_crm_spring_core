package com.hitruk.storage;

import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.TrainerSpecialization;
import com.hitruk.gym.crm.storage.IdGenerator;
import com.hitruk.gym.crm.storage.TrainerStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerStorageTest {
    private TrainerStorage storage;

    @BeforeEach
    void setUp() {
        storage = new TrainerStorage();
        storage.setIdGenerator(new IdGenerator());
    }

    private Trainer trainer(String firstName, String lastName) {
        return Trainer.builder()
                .firstName(firstName)
                .lastName(lastName)
                .specialization(TrainerSpecialization.FITNESS)
                .build();
    }

    @Test
    void create_assignsSequentialIds() {
        Trainer t1 = storage.create(trainer("Chris", "Bumstead"));
        Trainer t2 = storage.create(trainer("Mat", "Fraser"));
        Trainer t3 = storage.create(trainer("Jeff", "Nippard"));

        assertEquals(1L, t1.getId());
        assertEquals(2L, t2.getId());
        assertEquals(3L, t3.getId());
    }

    @Test
    void create_returnsSameEntityReference() {
        Trainer input = trainer("Chris", "Bumstead");

        assertSame(input, storage.create(input));
    }

    @Test
    void create_doesNotOverwriteExistingUsername() {
        Trainer t = trainer("Chris", "Bumstead");
        t.setUsername("Chris.Bumstead");
        t.setPassword("pass");

        storage.create(t);

        assertEquals("Chris.Bumstead", t.getUsername());
        assertEquals("pass", t.getPassword());
    }

    @Test
    void findById_afterCreate_returnsEntity() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));

        Trainer found = storage.findById(created.getId());

        assertNotNull(found);
        assertEquals("Chris", found.getFirstName());
    }

    @Test
    void findById_nonExistingId_returnsNull() {
        assertNull(storage.findById(999L));
    }

    @Test
    void findById_afterDelete_returnsNull() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));
        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
    }

    @Test
    void findAll_emptyStorage_returnsEmptyList() {
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void findAll_returnsAllStoredEntities() {
        storage.create(trainer("Chris", "Bumstead"));
        storage.create(trainer("Mat", "Fraser"));

        assertEquals(2, storage.findAll().size());
    }

    @Test
    void findAll_returnsImmutableList() {
        storage.create(trainer("Chris", "Bumstead"));

        List<Trainer> list = storage.findAll();

        assertThrows(UnsupportedOperationException.class,
                () -> list.add(trainer("Hacker", "Attack")));
    }

    @Test
    void findAll_afterDelete_doesNotContainDeletedEntity() {
        Trainer t1 = storage.create(trainer("Chris", "Bumstead"));
        Trainer t2 = storage.create(trainer("Mat", "Fraser"));

        storage.delete(t1.getId());

        List<Trainer> all = storage.findAll();
        assertEquals(1, all.size());
        assertEquals(t2.getId(), all.getFirst().getId());
    }

    @Test
    void update_replacesEntityAndSetsId() {
        Trainer original = storage.create(trainer("Chris", "Bumstead"));
        Trainer replacement = trainer("Updated", "Name");

        storage.update(original.getId(), replacement);

        Trainer found = storage.findById(original.getId());
        assertEquals("Updated", found.getFirstName());
        assertEquals(original.getId(), found.getId());
    }

    @Test
    void update_returnsProvidedEntity() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));
        Trainer replacement = trainer("Updated", "Name");

        Trainer result = storage.update(created.getId(), replacement);

        assertSame(replacement, result);
    }

    @Test
    void update_preservesSpecialization() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));
        Trainer replacement = Trainer.builder()
                .firstName("Chris")
                .lastName("Bumstead")
                .specialization(TrainerSpecialization.BODYBUILDING)
                .build();

        storage.update(created.getId(), replacement);

        assertEquals(TrainerSpecialization.BODYBUILDING,
                storage.findById(created.getId()).getSpecialization());
    }

    @Test
    void delete_existingEntity_returnsTrue() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));

        assertTrue(storage.delete(created.getId()));
    }

    @Test
    void delete_nonExistingId_returnsFalse() {
        assertFalse(storage.delete(999L));
    }

    @Test
    void delete_removesEntityFromStorage() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));

        storage.delete(created.getId());

        assertNull(storage.findById(created.getId()));
        assertTrue(storage.findAll().isEmpty());
    }

    @Test
    void delete_calledTwice_secondCallReturnsFalse() {
        Trainer created = storage.create(trainer("Chris", "Bumstead"));

        storage.delete(created.getId());

        assertFalse(storage.delete(created.getId()));
    }
}
