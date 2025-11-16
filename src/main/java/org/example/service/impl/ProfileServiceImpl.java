package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.ProfileSavableDao;
import org.example.model.User;
import org.example.service.ProfileService;
import org.example.util.ProfileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private ProfileSavableDao profileSavableDao;

    @Autowired
    public void setProfileSavableDao(ProfileSavableDao profileSavableDao) {
        this.profileSavableDao = profileSavableDao;
    }

    @Override
    public void createProfile(User user) {
        log.info("Creating profile for user: {} {}", user.getFirstName(), user.getLastName());

        List<String> existingUsernames = profileSavableDao.findAllUsernames();

        String username = ProfileGenerator.generateUsername(
                user.getFirstName(),
                user.getLastName(),
                existingUsernames
        );
        char[] password = ProfileGenerator.generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);

        profileSavableDao.save(user);

        log.info("Profile created successfully for username: {}", username);
    }
}
