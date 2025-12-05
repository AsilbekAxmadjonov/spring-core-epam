package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserEntityService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserEntityServiceImpl implements UserEntityService {

    private final UserRepo userRepo;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public User getByUsername(String username) {
        log.info("Fetching user by username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return userMapper.toModel(entity);
    }

    @Override
    public User createUser(User user) {
        log.info("Creating new user with username: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(new String(user.getPassword())).toCharArray());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepo.save(entity);
        return userMapper.toModel(saved);
    }

    @Override
    public User updateUser(String username, User updatedUser) {
        log.info("Updating user with username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        entity.setFirstName(updatedUser.getFirstName());
        entity.setLastName(updatedUser.getLastName());
        entity.setPassword(updatedUser.getPassword());
        entity.setIsActive(updatedUser.isActive());

        entity.setPassword(passwordEncoder.encode(new String(updatedUser.getPassword())).toCharArray());

        UserEntity saved = userRepo.save(entity);
        return userMapper.toModel(saved);
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user with username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        userRepo.delete(entity);
    }

    @Override
    public List<User> getAllUsers() {
        log.info("Fetching all users");

        List<UserEntity> entities = userRepo.findAll();
        return userMapper.toModels(entities);
    }

    @Override
    public User changeActiveStatus(String username, boolean isActive) {
        log.info("Changing active status of {} to {}", username, isActive);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        entity.setIsActive(isActive);

        UserEntity saved = userRepo.save(entity);
        return userMapper.toModel(saved);
    }

    @Override
    public User authenticate(String username, char[] rawPassword) {
        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        if (!passwordEncoder.matches(new String(rawPassword), new String(entity.getPassword()))) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        return userMapper.toModel(entity);
    }

}
