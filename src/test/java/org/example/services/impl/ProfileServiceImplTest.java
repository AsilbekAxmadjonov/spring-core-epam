package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainee;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

    @BeforeEach
    void setup() {
        userService = mock(UserService.class);
        userRepo = mock(UserRepo.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new ProfileServiceImpl(userService, userRepo, passwordEncoder);

        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED_PASS");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
    }

    /* ================= CREATE PROFILE ================= */

    @Test
    void createProfile_generatesUsernamePasswordAndActivatesUser() {
        User user = new Trainee();
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userService.fetchAll()).thenReturn(new ArrayList<>());
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
    void createProfile_appendsIndexWhenUsernameExists() {
        User user = new Trainee();
        user.setFirstName("John");
        user.setLastName("Doe");

        User existing = new Trainee();
        existing.setUsername("John.Doe");

        when(userService.fetchAll()).thenReturn(List.of(existing));
        when(userService.createUser(any(User.class))).thenReturn(user);

        service.createProfile(user);

        assertEquals("John.Doe1", user.getUsername());
    }

    /* ================= PASSWORD MATCHES ================= */

    @Test
    void passwordMatches_returnsTrue_whenPasswordMatches() {
        UserEntity entity = UserEntity.builder()
                .username("john.doe")
                .password("ENCODED_PASS".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(entity));

        boolean result = service.passwordMatches("john.doe", "raw".toCharArray());

        assertTrue(result);
        verify(passwordEncoder)
                .matches("raw", "ENCODED_PASS");
    }

    @Test
    void passwordMatches_returnsFalse_whenPasswordDoesNotMatch() {
        UserEntity entity = UserEntity.builder()
                .username("john.doe")
                .password("ENCODED_PASS".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(entity));
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false);

        boolean result = service.passwordMatches("john.doe", "wrong".toCharArray());

        assertFalse(result);
    }

    @Test
    void passwordMatches_throwsException_whenUserNotFound() {
        when(userRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.passwordMatches("unknown", "pass".toCharArray()));
    }

    /* ================= CHANGE PASSWORD ================= */

    @Test
    void changePassword_updatesPassword_whenUserExists() {
        UserEntity entity = UserEntity.builder()
                .username("john.doe")
                .password("OLD".toCharArray())
                .build();

        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(entity));

        service.changePassword("john.doe", "newPass".toCharArray());

        assertArrayEquals("ENCODED_PASS".toCharArray(), entity.getPassword());
        verify(userRepo).save(entity);
        verify(passwordEncoder).encode("newPass");
    }

    @Test
    void changePassword_throwsException_whenUserNotFound() {
        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.changePassword("john.doe", "new".toCharArray()));
    }

    /* ================= TOGGLE ACTIVE ================= */

    @Test
    void toggleUserActiveStatus_switchesFromFalseToTrue() {
        UserEntity entity = UserEntity.builder()
                .username("john.doe")
                .isActive(false)
                .build();

        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(entity));

        boolean result = service.toggleUserActiveStatus("john.doe");

        assertTrue(result);
        assertTrue(entity.getIsActive());
        verify(userRepo).save(entity);
    }

    @Test
    void toggleUserActiveStatus_switchesFromTrueToFalse() {
        UserEntity entity = UserEntity.builder()
                .username("john.doe")
                .isActive(true)
                .build();

        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(entity));

        boolean result = service.toggleUserActiveStatus("john.doe");

        assertFalse(result);
        assertFalse(entity.getIsActive());
        verify(userRepo).save(entity);
    }

    @Test
    void toggleUserActiveStatus_throwsException_whenUserNotFound() {
        when(userRepo.findByUsername("john.doe"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.toggleUserActiveStatus("john.doe"));
    }
}
