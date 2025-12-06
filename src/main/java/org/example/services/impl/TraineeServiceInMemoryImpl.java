package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.services.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TraineeServiceInMemoryImpl implements TraineeService {

    private TraineeDao traineeDao;

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public Trainee createTrainee(Trainee trainee) {
        log.info("Creating new Trainee: {}", trainee.getUsername());
        traineeDao.save(trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String username) {
        log.debug("Getting Trainee by username: {}", username);
        return Optional.ofNullable(traineeDao.findByUsername(username));
    }

    @Override
    public Trainee updateTrainee(String username, Trainee trainee) {
        log.info("Updating Trainee: {}", username);
        traineeDao.update(trainee);
        return trainee;
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee != null) {
            traineeDao.delete(trainee);
        }
    }

    @Override
    public boolean passwordMatches(String username, char[] password) {
        Trainee trainee = traineeDao.findByUsername(username);
        return trainee != null && java.util.Arrays.equals(trainee.getPassword(), password);
    }

    @Override
    public Trainee changePassword(String username, char[] newPassword) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee != null) {
            trainee.setPassword(newPassword);
            traineeDao.update(trainee);
        }
        return trainee;
    }

    @Override
    public Trainee setActiveStatus(String username, boolean active) {
        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee != null) {
            trainee.setActive(active);
            traineeDao.update(trainee);
        }
        return trainee;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.info("Listing all Trainees");
        return traineeDao.findAll();
    }
}
