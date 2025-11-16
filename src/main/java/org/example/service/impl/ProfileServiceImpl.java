package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.model.User;
import org.example.service.ProfileService;
import org.example.service.UserService;
import org.example.util.ProfileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private UserService userService;

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void createProfile(User user) {
        log.info("Creating profile for user: {} {}", user.getFirstName(), user.getLastName());

        List<User> existingUsers = userService.findAll();

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
}
