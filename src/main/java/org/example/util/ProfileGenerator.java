package org.example.util;

import org.example.model.User;

import java.security.SecureRandom;
import java.util.List;

public class ProfileGenerator {

    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    // Generate username from first and last name
    public static String generateUsername(String firstName, String lastName, List<User> existingUsers) {
        String baseUsername = firstName + "." + lastName;
        String username = baseUsername;

        int counter = 1;
        boolean exists = true;
        while (exists) {
            exists = false;
            for (User u : existingUsers) {
                if (username.equals(u.getUsername())) {
                    exists = true;
                    username = baseUsername + counter;
                    counter++;
                    break;
                }
            }
        }

        return username;
    }


    // Generate random password using ASCII math
    public static char[] generateRandomPassword() {
        char[] password = new char[PASSWORD_LENGTH];

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int type = random.nextInt(3); // 0=upper, 1=lower, 2=digit
            switch (type) {
                case 0 -> password[i] = (char) ('A' + random.nextInt(26));
                case 1 -> password[i] = (char) ('a' + random.nextInt(26));
                case 2 -> password[i] = (char) ('0' + random.nextInt(10));
            }
        }
        return password;
    }
}