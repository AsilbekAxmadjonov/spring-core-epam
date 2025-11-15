package utilTest;

import org.example.util.ProfileGenerator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfileGeneratorTest {

    @Test
    void testGenerateUsernameUniqueness() {
        List<String> existing = List.of("Alice.Smith", "Alice.Smith1");
        String username = ProfileGenerator.generateUsername("Alice", "Smith", existing);
        assertEquals("Alice.Smith2", username);
    }

    @Test
    void testGenerateRandomPasswordLength() {
        char[] password = ProfileGenerator.generateRandomPassword();
        assertEquals(10, password.length);
    }
}
