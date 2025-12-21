package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistance.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.UserMapper;
import org.example.persistance.model.User;
import org.example.persistance.repository.UserRepo;
import org.example.services.AuthenticationService;
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
    public User authenticate(String username, char[] password) {
        log.debug("Authenticating user: {}", username);

        UserEntity userEntity = userRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Invalid username or password"));

        boolean matches = passwordEncoder.matches(
                new String(password),
                new String(userEntity.getPassword())
        );

        if (!matches) {
            throw new BadCredentialsException("Invalid username or password");  // Changed this line
        }

        log.info("User authenticated successfully: {}", username);
        return userMapper.toModel(userEntity);
    }
}
