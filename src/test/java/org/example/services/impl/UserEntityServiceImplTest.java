package org.example.services.impl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserEntityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserEntityServiceImplTest {

    private UserRepo userRepo;
    private UserMapper userMapper;
    private UserEntityService service;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userMapper = mock(UserMapper.class);
        service = new UserEntityServiceImpl(userRepo, userMapper);
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

        when(userMapper.toEntity(model)).thenReturn(entity);
        when(userRepo.save(entity)).thenReturn(savedEntity);
        when(userMapper.toModel(savedEntity)).thenReturn(model);

        User result = service.createUser(model);

        assertNotNull(result);
        verify(userRepo).save(entity);
        verify(userMapper).toModel(savedEntity);
    }

    @Test
    void testUpdateUserSuccess() {
        User updated = User.builder()
                .firstName("New")
                .lastName("Name")
                .username("john")
                .password("newpass123".toCharArray())
                .isActive(false)
                .build();

        UserEntity entity = new UserEntity();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(updated);

        User result = service.updateUser("john", updated);

        assertNotNull(result);
        assertEquals("New", entity.getFirstName());
        assertEquals("Name", entity.getLastName());
        assertArrayEquals("newpass123".toCharArray(), entity.getPassword());
        assertFalse(entity.getIsActive());

        verify(userRepo).save(entity);
    }

    @Test
    void testUpdateUserNotFound() {
        User updated = new User();
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.updateUser("unknown", updated)
        );
    }

    // ------------------------------------------------------------
    //  deleteUser()
    // ------------------------------------------------------------
    @Test
    void testDeleteUserSuccess() {
        UserEntity entity = new UserEntity();
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));

        service.deleteUser("john");

        verify(userRepo).delete(entity);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.deleteUser("unknown")
        );
    }

    // ------------------------------------------------------------
    //  getAllUsers()
    // ------------------------------------------------------------
    @Test
    void testGetAllUsers() {
        List<UserEntity> entities = List.of(new UserEntity(), new UserEntity());
        List<User> models = List.of(new User(), new User());

        when(userRepo.findAll()).thenReturn(entities);
        when(userMapper.toModels(entities)).thenReturn(models);

        List<User> result = service.getAllUsers();

        assertEquals(2, result.size());
        verify(userRepo).findAll();
        verify(userMapper).toModels(entities);
    }

    // ------------------------------------------------------------
    //  changeActiveStatus()
    // ------------------------------------------------------------
    @Test
    void testChangeActiveStatus() {
        UserEntity entity = new UserEntity();
        entity.setIsActive(false);

        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.changeActiveStatus("john", true);

        assertNotNull(result);
        assertTrue(entity.getIsActive());
        verify(userRepo).save(entity);
    }

    @Test
    void testChangeActiveStatusNotFound() {
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.changeActiveStatus("unknown", true)
        );
    }
}
