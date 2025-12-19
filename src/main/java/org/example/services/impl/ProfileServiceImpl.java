package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.ProfileService;
import org.example.services.UserService;
import org.example.util.ProfileGenerator;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserService userService;
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public void createProfile(User user) {

        MDC.put("operation", "Create User Profile");
        MDC.put("username", user.getUsername());

        log.info("Creating profile for user: {} {}", user.getFirstName(), user.getLastName());

        List<User> existingUsers = userService.fetchAll();

        String username = ProfileGenerator.generateUsername(
                user.getFirstName(),
                user.getLastName(),
                existingUsers
        );

        char[] rawPassword = ProfileGenerator.generateRandomPassword();
        char[] encodedPassword = passwordEncoder.encode(new String(rawPassword)).toCharArray();

        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setIsActive(true);

        userService.createUser(user);

        log.info("Profile created successfully for username: {}", username);
    }

    @Override
    public boolean passwordMatches(String username, char[] rawPassword) {
        log.debug("Checking password for username: {}", username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        return passwordEncoder.matches(
                new String(rawPassword),
                new String(user.getPassword())
        );
    }

    @Override
    @Transactional
    public void changePassword(String username, char[] newPassword) {

//        String authenticated = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticated == null || !authenticated.equals(username)) {
//            throw new SecurityException("User not authenticated");
//        }

        log.debug("Changing password for {}", username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        user.setPassword(
                passwordEncoder.encode(new String(newPassword)).toCharArray()
        );

        userRepo.save(user);

        log.info("Password updated for {}", username);
    }

    @Override
    @Transactional
    public boolean toggleUserActiveStatus(String username) {

//        String authenticated = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticated == null || !authenticated.equals(username)) {
//            throw new SecurityException("User not authenticated");
//        }

        log.debug("Toggling active status for {}", username);

        UserEntity user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        boolean newStatus = !user.getIsActive();
        user.setIsActive(newStatus);

        userRepo.save(user);

        log.info("Active status toggled for {} -> {}", username, newStatus);

        return newStatus;
    }
}