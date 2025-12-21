package org.example.security.service;

import org.example.persistance.model.User;
import org.example.security.GymUserDetails;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymUserDetailsServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private GymUserDetailsService gymUserDetailsService;

    private User testUser;
    private static final String TEST_USERNAME = "testuser";
    private static final char[] TEST_PASSWORD = "password123".toCharArray();

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername(TEST_USERNAME);
        testUser.setPassword(TEST_PASSWORD);
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Arrange
        when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);

        // Act
        UserDetails result = gymUserDetailsService.loadUserByUsername(TEST_USERNAME);

        // Assert
        assertNotNull(result);
        assertInstanceOf(GymUserDetails.class, result);
        assertEquals(TEST_USERNAME, result.getUsername());
        verify(userService, times(1)).getByUsername(TEST_USERNAME);
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String nonExistentUsername = "nonexistent";
        when(userService.getByUsername(nonExistentUsername))
                .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername(nonExistentUsername)
        );

        assertTrue(exception.getMessage().contains("User not found: " + nonExistentUsername));
        verify(userService, times(1)).getByUsername(nonExistentUsername);
    }

    @Test
    void loadUserByUsername_WhenUserServiceThrowsException_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userService.getByUsername(TEST_USERNAME))
                .thenThrow(new IllegalArgumentException("Database error"));

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername(TEST_USERNAME)
        );

        assertTrue(exception.getMessage().contains("User not found: " + TEST_USERNAME));
        assertNotNull(exception.getCause());
        verify(userService, times(1)).getByUsername(TEST_USERNAME);
    }

    @Test
    void loadUserByUsername_WhenUsernameIsNull_ShouldThrowUsernameNotFoundException() {
        // Arrange
        when(userService.getByUsername(null))
                .thenThrow(new IllegalArgumentException("Username cannot be null"));

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername(null)
        );

        verify(userService, times(1)).getByUsername(null);
    }

    @Test
    void loadUserByUsername_WhenUsernameIsEmpty_ShouldThrowUsernameNotFoundException() {
        // Arrange
        String emptyUsername = "";
        when(userService.getByUsername(emptyUsername))
                .thenThrow(new RuntimeException("Username is empty"));

        // Act & Assert
        assertThrows(
                UsernameNotFoundException.class,
                () -> gymUserDetailsService.loadUserByUsername(emptyUsername)
        );

        verify(userService, times(1)).getByUsername(emptyUsername);
    }

    @Test
    void loadUserByUsername_ShouldWrapReturnedUserInGymUserDetails() {
        // Arrange
        when(userService.getByUsername(TEST_USERNAME)).thenReturn(testUser);

        // Act
        UserDetails result = gymUserDetailsService.loadUserByUsername(TEST_USERNAME);

        // Assert
        assertInstanceOf(GymUserDetails.class, result);
        GymUserDetails gymUserDetails = (GymUserDetails) result;
        assertEquals(testUser.getUsername(), gymUserDetails.getUsername());
    }
}