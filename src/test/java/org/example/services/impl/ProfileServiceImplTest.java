package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.AuthenticationService;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ProfileServiceImpl service;
    private UserService userService;
    private UserRepo userRepo;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        userRepo = mock(UserRepo.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationService = mock(AuthenticationService.class);

        service = new ProfileServiceImpl(userService, userRepo, passwordEncoder, authenticationService);

        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASS");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    }

    @Test
    void testCreateProfileAssignsUsernameAndPassword() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        User existingUser = new Trainee();
        existingUser.setUsername("John.Doe");
        when(userService.fetchAll()).thenReturn(List.of(existingUser));

        service.createProfile(trainee);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).save(captor.capture());

        User saved = captor.getValue();

        assertEquals("John.Doe1", saved.getUsername());
        assertEquals("ENCODED_PASS".length(), saved.getPassword().length);
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

        char[] oldPassword = "oldPass".toCharArray();
        char[] newPassword = "newPass".toCharArray();

        User mockUser = new Trainee();
        mockUser.setUsername("john.doe");
        when(authenticationService.authenticate("john.doe", oldPassword)).thenReturn(mockUser);

        service.changePassword("john.doe", oldPassword, newPassword);

        assertArrayEquals("ENCODED_PASS".toCharArray(), userEntity.getPassword());
        verify(authenticationService).authenticate("john.doe", oldPassword);
        verify(userRepo).save(userEntity);
    }

    @Test
    void testChangePasswordUserNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());
        char[] oldPassword = "old".toCharArray();
        char[] newPassword = "new".toCharArray();

        assertThrows(UserNotFoundException.class,
                () -> service.changePassword("unknown", oldPassword, newPassword));
    }

    @Test
    void testSetActiveStatusUpdatesUser() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .isActive(false)
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));

        char[] password = "pass".toCharArray();
        User mockUser = new Trainee();
        mockUser.setUsername("john.doe");
        when(authenticationService.authenticate("john.doe", password)).thenReturn(mockUser);

        service.setActiveStatus("john.doe", password, true);

        verify(authenticationService).authenticate("john.doe", password);
        assertTrue(userEntity.getIsActive());
        verify(userRepo).save(userEntity);
    }

    @Test
    void testSetActiveStatusUserNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());
        char[] password = "pass".toCharArray();

        assertThrows(UserNotFoundException.class,
                () -> service.setActiveStatus("unknown", password, true));
    }

    @Test
    void testSetActiveStatusAlreadyActiveThrows() {
        UserEntity userEntity = UserEntity.builder()
                .username("john.doe")
                .isActive(true)
                .build();

        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));

        char[] password = "pass".toCharArray();

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> service.setActiveStatus("john.doe", password, true));
        assertEquals("User john.doe is already active", ex.getMessage());
    }
}