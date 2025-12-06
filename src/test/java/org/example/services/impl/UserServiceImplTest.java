package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private UserRepo userRepo;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserServiceDbImpl(userRepo, userMapper, passwordEncoder);
    }

    @Test
    void testGetByUsernameSuccess() {
        UserEntity entity = new UserEntity();
        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.getByUsername("john");

        assertNotNull(result);
        verify(userRepo).findByUsername("john");
        verify(userMapper).toModel(entity);
    }

    @Test
    void testGetByUsernameNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getByUsername("unknown")
        );
    }

    @Test
    void testCreateUser() {
        User model = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        UserEntity entity = new UserEntity();
        UserEntity savedEntity = new UserEntity();

        // Use anyString() to match any password input
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userMapper.toEntity(model)).thenReturn(entity);
        when(userRepo.save(entity)).thenReturn(savedEntity);
        when(userMapper.toModel(savedEntity)).thenReturn(model);

        User result = service.createUser(model);

        assertNotNull(result);
        verify(passwordEncoder).encode(anyString());
        verify(userRepo).save(entity);
        verify(userMapper).toModel(savedEntity);
    }

    @Test
    void testUpdateUserNotFound() {
        User updated = new User();
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.updateUser("unknown", updated)
        );
    }

    @Test
    void testDeleteByUsernameSuccess() {
        UserEntity entity = new UserEntity();
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));

        service.deleteByUsername("john");

        verify(userRepo).delete(entity);
    }

    @Test
    void testDeleteByUsernameNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.deleteByUsername("unknown")
        );
    }

    @Test
    void testFetchAll() {
        List<UserEntity> entities = List.of(new UserEntity(), new UserEntity());
        List<User> models = List.of(new User(), new User());

        when(userRepo.findAll()).thenReturn(entities);
        when(userMapper.toModels(entities)).thenReturn(models);

        List<User> result = service.fetchAll();

        assertEquals(2, result.size());
        verify(userRepo).findAll();
        verify(userMapper).toModels(entities);
    }

    @Test
    void testChangeUserActiveStatus() {
        UserEntity entity = new UserEntity();
        entity.setIsActive(false);

        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.changeUserActiveStatus("john", true);

        assertNotNull(result);
        assertTrue(entity.getIsActive());
        verify(userRepo).save(entity);
    }

    @Test
    void testChangeUserActiveStatusNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.changeUserActiveStatus("unknown", true)
        );
    }

    @Test
    void testAuthenticateSuccess() {
        UserEntity entity = new UserEntity();
        entity.setPassword("hashedPassword".toCharArray());

        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.authenticate("john", "password123".toCharArray());

        assertNotNull(result);
        verify(passwordEncoder).matches("password123", "hashedPassword");
    }

    @Test
    void testAuthenticateFail() {
        UserEntity entity = new UserEntity();
        entity.setPassword("hashedPassword".toCharArray());

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(passwordEncoder.matches("wrongPass", "hashedPassword")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> service.authenticate("john", "wrongPass".toCharArray()));
    }

    @Test
    void testAuthenticateNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.authenticate("unknown", "anyPass".toCharArray()));
    }
}
