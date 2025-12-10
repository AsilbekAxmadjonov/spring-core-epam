package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.AuthenticationService;
import org.example.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Primary
@Validated
@Transactional
public class UserServiceDbImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    public UserServiceDbImpl(
            UserRepo userRepo,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationService authenticationService) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationService = authenticationService;
    }


    @Override
    public User getByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Fetching user by username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        log.info("User fetched: {}", username);
        return userMapper.toModel(entity);
    }

    @Override
    public User getByUsername(String username) {
        log.debug("Fetching user by username (no auth): {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return userMapper.toModel(entity);
    }


    @Override
    public User createUser(@Valid User user) {
        log.debug("Creating new user with username: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(new String(user.getPassword())).toCharArray());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepo.save(entity);

        log.info("User created: {}", saved.getUsername());
        return userMapper.toModel(saved);
    }

    @Override
    public User updateUser(String username, char[] password, @Valid User updatedUser) {
        authenticationService.authenticate(username, password);

        log.debug("Updating user with username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        userMapper.updateEntityFromModel(updatedUser, entity);

        entity.setPassword(passwordEncoder.encode(new String(updatedUser.getPassword())).toCharArray());

        UserEntity saved = userRepo.save(entity);

        log.info("User updated: {}", username);
        return userMapper.toModel(saved);
    }

    @Override
    public void deleteByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Deleting user with username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        userRepo.delete(entity);

        log.info("User deleted: {}", username);
    }

    @Override
    public List<User> fetchAll() {
        log.debug("Fetching all users");

        List<UserEntity> entities = userRepo.findAll();

        log.info("Fetched {} users", entities.size());
        return userMapper.toModels(entities);
    }

    @Override
    public User changeUserActiveStatus(String username, char[] password, boolean isActive) {
        authenticationService.authenticate(username, password);

        log.debug("Changing active status of {} to {}", username, isActive);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        entity.setIsActive(isActive);

        UserEntity saved = userRepo.save(entity);

        log.info("Active status changed for {} to {}", username, isActive);
        return userMapper.toModel(saved);
    }

    @Override
    public void save(@Valid User user){
        throw new UnsupportedOperationException("Not supported in DB service");
    }


}