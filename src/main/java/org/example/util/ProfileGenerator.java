package org.example.util;

import java.security.SecureRandom;
import java.util.List;

public class ProfileGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    // Generate username from first and last name
    public static String generateUsername(String firstName, String lastName, List<String> existingUsernames) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        int counter = 1;
        while (existingUsernames.contains(username)) {
            username = baseUsername + counter;
            counter++;
        }
        return username;
    }

    // Generate random password
    public static String generateRandomPassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }
}
