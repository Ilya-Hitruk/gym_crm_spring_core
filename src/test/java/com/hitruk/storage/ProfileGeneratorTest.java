package com.hitruk.storage;

import com.hitruk.gym.crm.model.entity.Trainee;
import com.hitruk.gym.crm.storage.ProfileGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGeneratorTest {
    private ProfileGenerator profileGenerator;

    @BeforeEach
    void setUp() {
        profileGenerator = new ProfileGenerator();
    }

    @Test
    void generateUsername_noConflict_returnsBaseUsername() {
        String username = profileGenerator.generateUsername("John", "Smith", List.of());

        assertEquals("John.Smith", username);
    }

    @Test
    void generateUsername_oneConflict_appendsSuffix1() {
        Trainee existing = Trainee.builder().username("John.Smith").build();

        String username = profileGenerator.generateUsername("John", "Smith", List.of(existing));

        assertEquals("John.Smith1", username);
    }

    @Test
    void generateUsername_twoConflicts_appendsSuffix2() {
        Trainee e1 = Trainee.builder().username("John.Smith").build();
        Trainee e2 = Trainee.builder().username("John.Smith1").build();

        String username = profileGenerator.generateUsername("John", "Smith", List.of(e1, e2));

        assertEquals("John.Smith2", username);
    }

    @Test
    void generateUsername_differentNames_noSuffix() {
        Trainee existing = Trainee.builder().username("Jane.Doe").build();

        String username = profileGenerator.generateUsername("John", "Smith", List.of(existing));

        assertEquals("John.Smith", username);
    }

    @RepeatedTest(5)
    void generatePassword_always10Chars() {
        String password = profileGenerator.generatePassword();

        assertNotNull(password);
        assertEquals(10, password.length());
    }

    @RepeatedTest(5)
    void generatePassword_onlyAlphanumeric() {
        String password = profileGenerator.generatePassword();

        assertTrue(password.matches("[A-Za-z0-9]{10}"),
                "Password should be 10 alphanumeric chars, got: " + password);
    }

    @Test
    void generatePassword_twoCallsProbablyDifferent() {
        String p1 = profileGenerator.generatePassword();
        String p2 = profileGenerator.generatePassword();

        assertNotNull(p1);
        assertNotNull(p2);
    }
}
