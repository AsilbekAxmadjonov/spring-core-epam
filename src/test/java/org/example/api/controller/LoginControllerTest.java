package org.example.api.controller;

import org.example.api.dto.request.LoginRequest;
import org.example.api.dto.response.LoginResponse;
import org.example.security.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;


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
        ResponseEntity<LoginResponse> response = loginController.login(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        LoginResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Login successful", body.getMessage());
        assertEquals(username, body.getUsername());
        assertEquals(token, body.getToken());

        // Verify that password array is cleared
        for (char c : password) {
            assertEquals(' ', c);
        }

        verify(authService, times(1)).login(eq(username), anyString());
    }

    @Test
    void testLoginFailure() throws Exception {
        // Arrange
        String username = "user2";
        char[] password = "wrongpass".toCharArray();

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        when(authService.login(eq(username), anyString()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
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
        ResponseEntity<LoginResponse> response = loginController.logout();

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        LoginResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("Logged out successfully", body.getMessage());
        assertNull(body.getToken());
        assertNull(body.getUsername());

        verify(authService, times(1)).logout();
    }
}
