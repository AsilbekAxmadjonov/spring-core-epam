package org.example.controller;

import org.example.dto.request.LoginRequest;
import org.example.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess() throws Exception {
        // Arrange
        String username = "user1";
        char[] password = "password123".toCharArray();
        String token = "mocked-jwt-token";

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        when(authService.login(eq(username), anyString())).thenReturn(token);

        // Act
        ResponseEntity<?> response = loginController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals(token, body.get("token"));
        assertEquals("Bearer", body.get("type"));

        // Verify that password array is cleared
        for (char c : password) {
            assertEquals(' ', c);
        }

        verify(authService, times(1)).login(eq(username), anyString());
    }

    @Test
    void testLoginFailure() {
        // Arrange
        String username = "user2";
        char[] password = "wrongpass".toCharArray();

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        when(authService.login(eq(username), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act + Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> loginController.login(request)
        );

        assertEquals("Invalid credentials", exception.getMessage());

        // Verify password is cleared
        for (char c : password) {
            assertEquals(' ', c);
        }

        verify(authService, times(1)).login(eq(username), anyString());
    }


    @Test
    void testLogout() {
        // Act
        ResponseEntity<?> response = loginController.logout();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Logged out successfully", body.get("message"));

        verify(authService, times(1)).logout();
    }
}
