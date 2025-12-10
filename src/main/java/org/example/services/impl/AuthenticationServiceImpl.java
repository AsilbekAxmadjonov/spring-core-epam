package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.repository.UserRepo;
import org.example.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public User authenticate(String username, char[] rawPassword) {
        log.debug("Authenticating user: {}", username);

        UserEntity entity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + username));

        boolean matches = passwordEncoder.matches(
                new String(rawPassword),
                new String(entity.getPassword())
        );

        if (!matches) {
            log.debug("Authentication failed: {}", username);
            throw new BadCredentialsException("Invalid username or password");
        }

        log.info("Authentication successful: {}", username);
        return userMapper.toModel(entity);
    }
}
