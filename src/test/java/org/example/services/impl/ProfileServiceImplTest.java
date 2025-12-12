package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.example.services.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ProfileServiceImpl service;
    private UserService userService;
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private MockedStatic<AuthenticationContext> authContextMock;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        userRepo = mock(UserRepo.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new ProfileServiceImpl(userService, userRepo, passwordEncoder);

        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASS");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Mock the static AuthenticationContext
        authContextMock = mockStatic(AuthenticationContext.class);
    }

    @AfterEach
    void tearDown() {
        // Close the static mock after each test
        authContextMock.close();
    }

    @Test
    void testCreateProfileGeneratesUsernameAndPassword() {
        User user = new Trainee();
        user.setFirstName("John");
        user.setLastName("Doe");

        List<User> existingUsers = new ArrayList<>();
        when(userService.fetchAll()).thenReturn(existingUsers);
        when(userService.createUser(any(User.class))).thenReturn(user);

        service.createProfile(user);

        assertNotNull(user.getUsername());
        assertTrue(user.getUsername().startsWith("John.Doe"));
        assertNotNull(user.getPassword());
        assertTrue(user.getIsActive());

        verify(userService).fetchAll();
        verify(userService).createUser(user);
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    void testCreateProfileWithExistingUsers() {
        User user = new Trainee();
        user.setFirstName("John");
        user.setLastName("Doe");

        User existingUser = new Trainee();
        existingUser.setUsername("John.Doe");

        List<User> existingUsers = List.of(existingUser);
        when(userService.fetchAll()).thenReturn(existingUsers);
        when(userService.createUser(any(User.class))).thenReturn(user);

        service.createProfile(user);

        assertNotNull(user.getUsername());
        assertEquals("John.Doe1", user.getUsername());

        verify(userService).fetchAll();
        verify(userService).createUser(user);
    }

    @Test
    void testPasswordMatchesReturnsTrue() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .password("ENCODED_PASS".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));

        boolean matches = service.passwordMatches("john.doe", "rawPass".toCharArray());

        assertTrue(matches);
        verify(passwordEncoder).matches("rawPass", "ENCODED_PASS");
    }

    @Test
    void testPasswordMatchesReturnsFalse() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .password("ENCODED_PASS".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        boolean matches = service.passwordMatches("john.doe", "wrongPass".toCharArray());

        assertFalse(matches);
    }

    @Test
    void testPasswordMatchesUserNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.passwordMatches("unknown", "pass".toCharArray()));
    }

    @Test
    void testChangePasswordUpdatesPassword() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .password("OLD_PASS".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");

        char[] newPassword = "newPass".toCharArray();

        service.changePassword("john.doe", newPassword);

        assertArrayEquals("ENCODED_PASS".toCharArray(), userEntity.getPassword());
        verify(userRepo).save(userEntity);
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    void testChangePasswordUserNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        char[] newPassword = "new".toCharArray();

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.changePassword("john.doe", newPassword));

        assertEquals("User not authenticated", ex.getMessage());
    }

    @Test
    void testChangePasswordDifferentUserAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        char[] newPassword = "new".toCharArray();

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.changePassword("john.doe", newPassword));

        assertEquals("User not authenticated", ex.getMessage());
    }

    @Test
    void testChangePasswordUserNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        char[] newPassword = "new".toCharArray();

        assertThrows(UserNotFoundException.class,
                () -> service.changePassword("unknown", newPassword));
    }

    @Test
    void testToggleUserActiveStatusFromInactiveToActive() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .isActive(false)
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");

        boolean newStatus = service.toggleUserActiveStatus("john.doe");

        assertTrue(newStatus);
        assertTrue(userEntity.getIsActive());
        verify(userRepo).save(userEntity);
    }

    @Test
    void testToggleUserActiveStatusFromActiveToInactive() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .isActive(true)
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");

        boolean newStatus = service.toggleUserActiveStatus("john.doe");

        assertFalse(newStatus);
        assertFalse(userEntity.getIsActive());
        verify(userRepo).save(userEntity);
    }

    @Test
    void testToggleUserActiveStatusUserNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.toggleUserActiveStatus("john.doe"));

        assertEquals("User not authenticated", ex.getMessage());
    }

    @Test
    void testToggleUserActiveStatusDifferentUserAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.toggleUserActiveStatus("john.doe"));

        assertEquals("User not authenticated", ex.getMessage());
    }

    @Test
    void testToggleUserActiveStatusUserNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.toggleUserActiveStatus("unknown"));
    }
}