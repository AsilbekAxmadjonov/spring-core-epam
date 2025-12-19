package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.UserService;
import org.slf4j.MDC;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private boolean shouldEnforceSecurity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() ||
                auth instanceof AnonymousAuthenticationToken) {
            log.debug("No authenticated user - skipping security check (authentication flow)");
            return false;
        }

        log.debug("User authenticated: {} - enforcing security check", auth.getName());
        return true;
    }

    private void verifyUserAccess(String requestedUsername) {
        if (!shouldEnforceSecurity()) {
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String authenticatedUsername = auth.getName();

        if (!authenticatedUsername.equals(requestedUsername)) {
            log.warn("Security violation: User {} attempted to access data for user {}",
                    authenticatedUsername, requestedUsername);
            throw new SecurityException("Access denied: Cannot access other user's data");
        }

        log.debug("Security check passed: User {} accessing own data", authenticatedUsername);
    }

    @Override
    @Transactional(readOnly = true)
    public User getByUsername(String username) {
        log.debug("Fetching user by username: {}", username);

        verifyUserAccess(username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        log.debug("User found: {}", username);
        return userMapper.toModel(entity);
    }

    @Override
    public User createUser(User user) {
        MDC.put("operation", "Create User");
        MDC.put("username", user.getUsername());

        log.debug("Creating new user with username: {}", user.getUsername());

        user.setPassword(passwordEncoder.encode(new String(user.getPassword())).toCharArray());

        UserEntity entity = userMapper.toEntity(user);
        UserEntity saved = userRepo.save(entity);

        log.info("User created: {}", saved.getUsername());
        return userMapper.toModel(saved);
    }

    @Override
    @Transactional
    public User updateUser(String username,User updatedUser) {
        MDC.put("operation", "Update User");
        MDC.put("username", username);

        log.debug("Updating user: {}", username);

        verifyUserAccess(username);

        UserEntity userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for update: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });

        if (updatedUser.getPassword() != null) {
            char[] encodedPassword = passwordEncoder.encode(
                    new String(updatedUser.getPassword())
            ).toCharArray();
            updatedUser.setPassword(encodedPassword);
        }

        userMapper.updateEntityFromModel(updatedUser, userEntity);
        UserEntity saved = userRepo.save(userEntity);

        log.info("User updated: {}", username);
        return userMapper.toModel(saved);
    }

    @Override
    public void deleteByUsername(String username) {
        MDC.put("operation", "Delete User");
        MDC.put("username", username);

        log.debug("Deleting user with username: {}", username);

        verifyUserAccess(username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found for deletion: {}", username);
                    return new UserNotFoundException("User not found: " + username);
                });

        userRepo.delete(entity);

        log.info("User deleted: {}", username);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> fetchAll() {
        log.debug("Fetching all users");

        List<UserEntity> entities = userRepo.findAll();

        log.info("Fetched {} users", entities.size());
        return userMapper.toModels(entities);
    }

    @Override
    public void save(User user) {
        throw new UnsupportedOperationException("Not supported in DB service");
    }
}