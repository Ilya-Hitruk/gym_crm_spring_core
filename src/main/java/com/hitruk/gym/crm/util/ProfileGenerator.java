package com.hitruk.gym.crm.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProfileGenerator {
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String base = firstName + "." + lastName;
        if (!existingUsernames.contains(base)) {
            return base;
        }
        int counter = 1;
        while (existingUsernames.contains(base + counter)) {
            counter++;
        }
        return base + counter;
    }

    public String generatePassword() {
        return random.ints(10, 0, CHARS.length())
                .mapToObj(CHARS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
