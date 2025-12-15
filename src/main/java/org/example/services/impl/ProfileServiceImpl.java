package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.mapper.UserMapper;
import org.example.model.User;
import org.example.services.ProfileService;
import org.example.services.UserService;
import org.example.util.ProfileGenerator;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private UserService userService;
    private UserMapper userMapper;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createProfile(User user) {

        MDC.put("operation", "Create User profile");
        MDC.put("username", user.getUsername());

        log.info("Creating profile for user: {} {}", user.getFirstName(), user.getLastName());

        List<User> existingUsers = userService.fetchAll();

        String username = ProfileGenerator.generateUsername(
                user.getFirstName(),
                user.getLastName(),
                existingUsers
        );

        char[] password = ProfileGenerator.generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);

        userService.save(user);

        log.info("Profile created successfully for username: {}", username);
    }

    @Autowired
    public void setUserMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
}
