package com.hitruk.storage;

import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.model.entity.Trainer;
import com.hitruk.gym.crm.model.entity.Training;
import com.hitruk.gym.crm.storage.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IdGeneratorTest {
    private IdGenerator idGenerator;

    @BeforeEach
    void setUp() {
        idGenerator = new IdGenerator();
    }

    @Test
    void generate_firstCall_returnsOne() {
        assertEquals(1L, idGenerator.generate(Trainee.class));
    }

    @Test
    void generate_consecutiveCalls_returnsIncrementalIds() {
        assertEquals(1L, idGenerator.generate(Trainee.class));
        assertEquals(2L, idGenerator.generate(Trainee.class));
        assertEquals(3L, idGenerator.generate(Trainee.class));
    }

    @Test
    void generate_differentTypes_haveIndependentSequences() {
        assertEquals(1L, idGenerator.generate(Trainee.class));
        assertEquals(1L, idGenerator.generate(Trainer.class));
        assertEquals(1L, idGenerator.generate(Training.class));

        assertEquals(2L, idGenerator.generate(Trainee.class));
        assertEquals(2L, idGenerator.generate(Trainer.class));
        assertEquals(2L, idGenerator.generate(Training.class));
    }

    @Test
    void generate_sameTypeManyTimes_alwaysUnique() {
        long previous = -1;
        for (int i = 0; i < 100; i++) {
            long id = idGenerator.generate(Trainee.class);
            assertTrue(id > previous, "Expected id > " + previous + " but got " + id);
            previous = id;
        }
    }

    @Test
    void setId_resetsSequenceForType() {
        idGenerator.generate(Trainee.class);
        idGenerator.generate(Trainee.class);

        idGenerator.setId(Trainee.class, 10L);

        assertEquals(10L, idGenerator.generate(Trainee.class));
        assertEquals(11L, idGenerator.generate(Trainee.class));
    }

    @Test
    void setId_doesNotAffectOtherTypes() {
        idGenerator.generate(Trainee.class);
        idGenerator.generate(Trainer.class);

        idGenerator.setId(Trainee.class, 50L);

        assertEquals(50L, idGenerator.generate(Trainee.class));
        assertEquals(2L, idGenerator.generate(Trainer.class));
    }
}
