package org.example.service.impl;

import org.example.model.Trainee;
import org.example.model.User;
import org.example.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ProfileServiceImplTest {

    private ProfileServiceImpl service;
    private UserService userService;

    @BeforeEach
    void setup() {
        service = new ProfileServiceImpl();
        userService = Mockito.mock(UserService.class);
        service.setUserService(userService);
    }

    @Test
    void testCreateProfileAssignsUsernameAndPassword() {
        Trainee trainee = new Trainee();
        trainee.setFirstName("John");
        trainee.setLastName("Doe");

        // Simulate existing users
        User existingUser = new Trainee();
        existingUser.setUsername("John.Doe");
        when(userService.findAll()).thenReturn(List.of(existingUser));

        service.createProfile(trainee);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).save(captor.capture());

        User saved = captor.getValue();
        assertEquals("John.Doe1", saved.getUsername());
        assertEquals(10, saved.getPassword().length);
    }
}
