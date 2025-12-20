package org.example.services.impl.dbImpl;

import org.example.persistance.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.persistance.model.User;
import org.example.persistance.repository.UserRepo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @BeforeEach
    void setUp() {
        userRepo = mock(UserRepo.class);
        userMapper = mock(UserMapper.class);
        passwordEncoder = mock(PasswordEncoder.class);

        service = new UserServiceDbImpl(userRepo, userMapper, passwordEncoder);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createUser_shouldCreateUser() {
        User model = User.builder()
                .username("john")
                .password("password".toCharArray())
                .build();

        UserEntity entity = new UserEntity();
        UserEntity saved = new UserEntity();

        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userMapper.toEntity(model)).thenReturn(entity);
        when(userRepo.save(entity)).thenReturn(saved);
        when(userMapper.toModel(saved)).thenReturn(model);

        User result = service.createUser(model);

        assertNotNull(result);
        verify(passwordEncoder).encode(anyString());
        verify(userRepo).save(entity);
    }

    @Test
    void getByUsername_shouldReturnUser_whenExists() {
        UserEntity entity = new UserEntity();
        User model = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(userMapper.toModel(entity)).thenReturn(model);

        User result = service.getByUsername("john");

        assertNotNull(result);
    }

    @Test
    void getByUsername_shouldThrow_whenNotFound() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(
                org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> service.getByUsername("john")
        );
    }

    @Test
    void updateUser_shouldUpdate_whenAuthenticatedAndSameUser() {
        authenticate("john");

        UserEntity entity = new UserEntity();
        User updated = new User();
        updated.setPassword("new".toCharArray());

        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepo.save(entity)).thenReturn(entity);
        when(userMapper.toModel(entity)).thenReturn(updated);

        User result = service.updateUser("john", updated);

        assertNotNull(result);
        verify(userRepo).save(entity);
    }

    @Test
    void updateUser_shouldThrowUserNotFound_whenNotAuthenticated() {
        User updated = new User();

        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.updateUser("john", updated));
    }

    @Test
    void deleteByUsername_shouldDelete_whenAuthenticatedAndSameUser() {
        authenticate("john");

        UserEntity entity = new UserEntity();
        when(userRepo.findByUsername("john")).thenReturn(Optional.of(entity));

        service.deleteByUsername("john");

        verify(userRepo).delete(entity);
    }

    @Test
    void deleteByUsername_shouldThrowUserNotFound_whenNotAuthenticated() {
        when(userRepo.findByUsername("john")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.deleteByUsername("john"));
    }

    @Test
    void fetchAll_shouldReturnList() {
        List<UserEntity> entities = List.of(new UserEntity(), new UserEntity());
        List<User> models = List.of(new User(), new User());

        when(userRepo.findAll()).thenReturn(entities);
        when(userMapper.toModels(entities)).thenReturn(models);

        List<User> result = service.fetchAll();

        assertEquals(2, result.size());
    }

    private void authenticate(String username) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(username, null)
        );
    }
}
