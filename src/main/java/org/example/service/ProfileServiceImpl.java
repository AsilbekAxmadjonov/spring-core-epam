package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.User;
import org.example.util.ProfileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProfileServiceImpl implements ProfileService {

    private final TrainerDao trainerDao;
    private final TraineeDao traineeDao;

    @Autowired
    public ProfileServiceImpl(TrainerDao trainerDao, TraineeDao traineeDao) {
        this.trainerDao = trainerDao;
        this.traineeDao = traineeDao;
    }

    @Override
    public void createProfile(User user) {
        log.info("Creating profile for user: {} {}", user.getFirstName(), user.getLastName());

        List<String> existingUsernames;
        if (user instanceof Trainer) {
            existingUsernames = trainerDao.findAll().stream().map(Trainer::getUsername).toList();
        } else if (user instanceof Trainee) {
            existingUsernames = traineeDao.findAll().stream().map(Trainee::getUsername).toList();
        } else {
            throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getSimpleName());
        }

        String username = ProfileGenerator.generateUsername(user.getFirstName(), user.getLastName(), existingUsernames);
        char[] password = ProfileGenerator.generateRandomPassword();

        user.setUsername(username);
        user.setPassword(password);

        if (user instanceof Trainer trainer) {
            trainerDao.save(trainer);
        } else if (user instanceof Trainee trainee) {
            traineeDao.save(trainee);
        }

        log.info("Profile created successfully for username: {}", username);
    }
}
