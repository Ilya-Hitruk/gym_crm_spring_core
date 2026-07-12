package com.hitruk.gym.crm.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGeneratorTest {

    private ProfileGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new ProfileGenerator();
    }

    @Test
    void generateUsername_noConflict_returnsBaseUsername() {
        String username = generator.generateUsername("John", "Smith", List.of());

        assertEquals("John.Smith", username);
    }

    @Test
    void generateUsername_baseConflict_addsCounter() {
        String username = generator.generateUsername("John", "Smith", List.of("John.Smith"));

        assertEquals("John.Smith1", username);
    }

    @Test
    void generateUsername_multipleConflicts_incrementsCounter() {
        List<String> existing = List.of("John.Smith", "John.Smith1", "John.Smith2");

        String username = generator.generateUsername("John", "Smith", existing);

        assertEquals("John.Smith3", username);
    }

    @Test
    void generateUsername_differentUsersPresent_noConflict() {
        String username = generator.generateUsername("Jane", "Doe",
                List.of("John.Smith", "Chris.Bumstead"));

        assertEquals("Jane.Doe", username);
    }

    @Test
    void generateUsername_format_usesFirstNameDotLastName() {
        String username = generator.generateUsername("Arnold", "Schwarzenegger", List.of());

        assertEquals("Arnold.Schwarzenegger", username);
    }

    @Test
    void generatePassword_returnsExactlyTenChars() {
        String password = generator.generatePassword();

        assertEquals(10, password.length());
    }

    @Test
    void generatePassword_containsOnlyAlphanumericChars() {
        for (int i = 0; i < 20; i++) {
            String password = generator.generatePassword();
            assertTrue(password.matches("[A-Za-z0-9]+"),
                    "Password contains non-alphanumeric chars: " + password);
        }
    }

    @Test
    void generatePassword_eachCallProducesDifferentPassword() {
        String p1 = generator.generatePassword();
        String p2 = generator.generatePassword();
        String p3 = generator.generatePassword();

        assertFalse(p1.equals(p2) && p2.equals(p3),
                "Three consecutive passwords are identical — extremely unlikely");
    }

    @Test
    void generateUsername_counterStartsAtOne_notZero() {
        String username = generator.generateUsername("Alice", "Walker", List.of("Alice.Walker"));

        assertEquals("Alice.Walker1", username);
        assertNotEquals("Alice.Walker0", username);
    }
}