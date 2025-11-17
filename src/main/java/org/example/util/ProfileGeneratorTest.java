package org.example.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGeneratorTest {

    private List<TestUser> users;

    @BeforeEach
    void setUp() {
        users = new ArrayList<>();
    }

    @Test
    void testGenerateUsername_NoConflict() {
        String username = ProfileGenerator.generateUsername("John", "Doe", new ArrayList<>(users));
        assertEquals("John.Doe", username);
    }

    @Test
    void testGenerateUsername_WithConflict() {
        // Add a user with the same username
        users.add(new TestUser("John", "Doe", "John.Doe", new char[]{}, true));

        String username = ProfileGenerator.generateUsername("John", "Doe", new ArrayList<>(users));
        assertEquals("John.Doe1", username);

        // Add another conflicting user
        users.add(new TestUser("John", "Doe", "John.Doe1", new char[]{}, true));

        String username2 = ProfileGenerator.generateUsername("John", "Doe", new ArrayList<>(users));
        assertEquals("John.Doe2", username2);
    }

    @Test
    void testGenerateRandomPassword_Length() {
        char[] password = ProfileGenerator.generateRandomPassword();
        assertEquals(10, password.length);
    }

    @Test
    void testGenerateRandomPassword_ContainsValidChars() {
        char[] password = ProfileGenerator.generateRandomPassword();
        for (char c : password) {
            boolean isUpper = c >= 'A' && c <= 'Z';
            boolean isLower = c >= 'a' && c <= 'z';
            boolean isDigit = c >= '0' && c <= '9';
            assertTrue(isUpper || isLower || isDigit, "Password contains invalid character: " + c);
        }
    }
}
