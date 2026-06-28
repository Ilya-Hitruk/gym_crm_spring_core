package com.hitruk.gym.crm.storage;

import com.hitruk.gym.crm.model.entity.User;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class ProfileGenerator {
    private static final String CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public String generateUsername(String firstName, String lastName, Collection<? extends User> users) {
        String baseUsername = firstName + "." + lastName;

        boolean existsBase = users.stream()
                .anyMatch(user -> user.getUsername().equals(baseUsername));
        if (!existsBase) {
            return baseUsername;
        }

        int counter = 1;

        while (true) {
            String username = baseUsername + counter;
            boolean exists = users.stream()
                    .anyMatch(user -> user.getUsername().equals(username));

            if (!exists) {
                return username;
            }
            counter++;
        }
    }

    public String generatePassword() {
        SecureRandom random = new SecureRandom();
        return random.ints(10, 0, CHARS.length())
                .mapToObj(CHARS::charAt)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
