package org.example.service;

import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.util.ProfileGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {

    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeDao traineeDao;

    // Setter-based injection
    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address) {
        List<String> existingUsernames = traineeDao.findAll().stream()
                .map(Trainee::getUsername)
                .toList();

        String username = ProfileGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = ProfileGenerator.generateRandomPassword();

        Trainee trainee = new Trainee();
        trainee.setFirstName(firstName);
        trainee.setLastName(lastName);
        trainee.setUsername(username);
        trainee.setPassword(password);
        trainee.setDateOfBirth(dateOfBirth);
        trainee.setAddress(address);

        traineeDao.save(trainee);
    }

    @Override
    public void createTrainee(Trainee trainee) {
        logger.info("Creating new Trainee: {}", trainee.getUsername());
        traineeDao.save(trainee);
    }

    @Override
    public void updateTrainee(Trainee trainee) {
        logger.info("Updating Trainee: {}", trainee.getUsername());
        traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Trainee trainee) {
        logger.info("Deleted Trainee: {}", trainee.getUsername());
        traineeDao.delete(trainee);
    }

    @Override
    public Trainee getTrainee(String username) {
        logger.debug("Getting Trainee by username: {}", username);
        return traineeDao.findByUsername(username);
    }

    @Override
    public List<Trainee> listAll() {
        logger.info("Listing all Trainees");
        return traineeDao.findAll();
    }
}
