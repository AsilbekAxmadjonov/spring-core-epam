package org.example.services.impl.dbImpl;

import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceDbImplTest {

    private UserRepo userRepo;
    private UserMapper userMapper;
    private PasswordEncoder passwordEncoder;
    private UserServiceDbImpl service;
    private MockedStatic<AuthenticationContext> authContextMock;

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new UserServiceDbImpl(userRepo, userMapper, passwordEncoder);

        authContextMock = mockStatic(AuthenticationContext.class);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
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

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john");
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(updatedModel);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedNewPass");

        User result = service.updateUser("john", updatedModel);

        assertNotNull(result);
        verify(userRepo).findByUsername("john");
        verify(userMapper).updateEntityFromModel(updatedModel, entity);
        verify(userRepo).save(entity);
    }

    @Test
    void testUpdateUserNotAuthenticated() {
        User updated = new User();

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.updateUser("john", updated));

        assertEquals("User not authenticated", ex.getMessage());
    }

    @Test
    void testUpdateUserNotFound() {
        User updated = new User();
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.updateUser("unknown", updated));

        verify(userRepo).findByUsername("unknown");
    }

    @Test
    void testDeleteByUsernameSuccess() {
        UserEntity entity = new UserEntity();
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john");
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));

        service.deleteByUsername("john");

        verify(userRepo).findByUsername("john");
        verify(userRepo).delete(entity);
    }

    @Test
    void testDeleteByUsernameNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.deleteByUsername("john"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(userRepo, never()).findByUsername(anyString());
    }

    @Test
    void testDeleteByUsernameDifferentUser() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> service.deleteByUsername("john"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(userRepo, never()).findByUsername(anyString());
    }

    @Test
    void testDeleteByUsernameNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(userRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.deleteByUsername("unknown"));

        verify(userRepo).findByUsername("unknown");
        verify(userRepo, never()).delete(any());
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
}