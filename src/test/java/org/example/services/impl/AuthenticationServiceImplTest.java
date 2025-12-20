package org.example.services.impl;

import org.example.persistance.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.persistance.model.User;
import org.example.persistance.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthenticationServiceImpl service;

    @Test
    void testAuthenticateSuccess() {
        UserEntity entity = new UserEntity();
        entity.setPassword("hashedPassword".toCharArray());

        User model = new User();

        when(userRepo.findByUsername("john"))
                .thenReturn(Optional.of(entity));

        when(passwordEncoder.matches("password123", "hashedPassword"))
                .thenReturn(true);

        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.authenticate("john", "password123".toCharArray());

        assertNotNull(result);
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void testAuthenticateFail() {
        UserEntity entity = new UserEntity();
        entity.setPassword("hashedPassword".toCharArray());

        when(userRepo.findByUsername("john"))
                .thenReturn(Optional.of(entity));

        when(passwordEncoder.matches("wrongPass", "hashedPassword"))
                .thenReturn(false);

        assertThrows(
                BadCredentialsException.class,
                () -> service.authenticate("john", "wrongPass".toCharArray())
        );
    }

    @Test
    void testAuthenticateNotFound() {
        when(userRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.authenticate("unknown", "anyPass".toCharArray())
        );
    }
}
