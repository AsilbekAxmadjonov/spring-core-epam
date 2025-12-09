package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserService;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class UserServiceDbImpl implements UserService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        log.info("User fetched: {}", username);
        return userMapper.toModel(entity);
    }

    @Override
    public User createUser(User user) {
        log.debug("Creating new user with username: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(new String(user.getPassword())).toCharArray());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepo.save(entity);

        log.info("User created: {}", saved.getUsername());
        return userMapper.toModel(saved);
    }

    @Override
    public User updateUser(String username, User updatedUser) {
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
    public void deleteByUsername(String username) {
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
    public User changeUserActiveStatus(String username, boolean isActive) {
        log.debug("Changing active status of {} to {}", username, isActive);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        entity.setIsActive(isActive);

        UserEntity saved = userRepo.save(entity);

        log.info("Active status changed for {} to {}", username, isActive);
        return userMapper.toModel(saved);
    }

    @Override
    public User authenticate(String username, char[] rawPassword) {
        log.debug("Authenticating user: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(new String(rawPassword), new String(entity.getPassword()))) {
            log.debug("Authentication failed for {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("Authentication successful for {}", username);
        return userMapper.toModel(entity);
    }

    @Override
    public void save(User user){
        throw new UnsupportedOperationException("Not supported in in-memory service");
    }


}
