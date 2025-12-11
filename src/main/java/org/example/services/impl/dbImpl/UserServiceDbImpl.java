package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.example.services.UserService;
import org.springframework.context.annotation.Primary;
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

    public UserServiceDbImpl(
            UserRepo userRepo,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        log.debug("Fetching user by username (no auth): {}", username);

        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

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
    public User updateUser(String username, @Valid User updatedUser) {

        log.debug("Updating user with username: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        userMapper.updateEntityFromModel(updatedUser, entity);

        if (updatedUser.getPassword() != null && updatedUser.getPassword().length > 0) {
            entity.setPassword(
                    passwordEncoder.encode(new String(updatedUser.getPassword())).toCharArray()
            );
        }

        UserEntity saved = userRepo.save(entity);

        log.info("User updated: {}", username);

        return userMapper.toModel(saved);
    }


    @Override
    public void deleteByUsername(String username) {
        String authenticated = AuthenticationContext.getAuthenticatedUser();

        if (authenticated == null || !authenticated.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

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
    public void save(@Valid User user){
        throw new UnsupportedOperationException("Not supported in DB service");
    }


}