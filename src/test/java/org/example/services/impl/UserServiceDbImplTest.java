package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceDbImplTest {

    private UserRepo userRepo;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private AuthenticationService authenticationService;
    private UserServiceDbImpl service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);
        authenticationService = mock(AuthenticationService.class);

        service = new UserServiceDbImpl(userRepo, userMapper, passwordEncoder, authenticationService);
    }

    @Test
    void testGetByUsernameSuccess() {
        UserEntity entity = new UserEntity();
        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(model);

        // authenticate is void; no need to doNothing()
        User result = service.getByUsername("john", "password".toCharArray());

        assertNotNull(result);
        verify(authenticationService).authenticate("john", "password".toCharArray());
        verify(userRepo).findByUsername("john");
        verify(userMapper).toModel(entity);
    }

    @Test
    void testGetByUsernameNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getByUsername("unknown", "pass".toCharArray())
        );
        verify(authenticationService).authenticate("unknown", "pass".toCharArray());
        verify(userRepo).findByUsername("unknown");
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
    void testUpdateUserSuccess() {
        UserEntity entity = new UserEntity();
        User updatedModel = new User();
        updatedModel.setPassword("newPass".toCharArray());

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(updatedModel);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPass");

        User result = service.updateUser("john", "oldPass".toCharArray(), updatedModel);

        assertNotNull(result);
        verify(authenticationService).authenticate("john", "oldPass".toCharArray());
        verify(userRepo).findByUsername("john");
        verify(userMapper).updateEntityFromModel(updatedModel, entity);
        verify(userRepo).save(entity);
    }

    @Test
    void testUpdateUserNotFound() {
        User updated = new User();
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.updateUser("unknown", "pass".toCharArray(), updated)
        );
        verify(authenticationService).authenticate("unknown", "pass".toCharArray());
        verify(userRepo).findByUsername("unknown");
    }

    @Test
    void testDeleteByUsernameSuccess() {
        UserEntity entity = new UserEntity();
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));

        service.deleteByUsername("john", "pass".toCharArray());

        verify(authenticationService).authenticate("john", "pass".toCharArray());
        verify(userRepo).delete(entity);
    }

    @Test
    void testDeleteByUsernameNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.deleteByUsername("unknown", "pass".toCharArray())
        );
        verify(authenticationService).authenticate("unknown", "pass".toCharArray());
        verify(userRepo).findByUsername("unknown");
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
    void testChangeUserActiveStatusSuccess() {
        UserEntity entity = new UserEntity();
        entity.setIsActive(false);

        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.changeUserActiveStatus("john", "pass".toCharArray(), true);

        assertNotNull(result);
        assertTrue(entity.getIsActive());
        verify(authenticationService).authenticate("john", "pass".toCharArray());
        verify(userRepo).save(entity);
    }

    @Test
    void testChangeUserActiveStatusNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.changeUserActiveStatus("unknown", "pass".toCharArray(), true)
        );
        verify(authenticationService).authenticate("unknown", "pass".toCharArray());
        verify(userRepo).findByUsername("unknown");
    }
}
