package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.services.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
        log.debug("Starting creation of Trainee: {}", trainee.getUsername());

        traineeDao.save(trainee);

        log.info("Trainee created successfully: {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String username) {
        log.debug("Fetching Trainee by username: {}", username);

        Trainee trainee = traineeDao.findByUsername(username);

        if (trainee != null) {
            log.debug("Trainee found: {}", username);
        } else {
            log.debug("Trainee not found: {}", username);
        }

        return Optional.ofNullable(trainee);
    }

    @Override
    public Trainee updateTrainee(String username, Trainee updatedTrainee) {
        log.debug("Starting update for Trainee: {}", username);

        traineeDao.update(updatedTrainee);

        log.info("Trainee updated successfully: {}", username);
        return updatedTrainee;
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        log.debug("Attempting to delete Trainee: {}", username);

        Trainee trainee = traineeDao.findByUsername(username);

        if (trainee != null) {
            traineeDao.delete(trainee);
            log.info("Trainee deleted: {}", username);
        } else {
            log.debug("Delete skipped – Trainee not found: {}", username);
        }
    }

    @Override
    public boolean passwordMatches(String username, char[] password) {
        log.debug("Checking password for Trainee: {}", username);

        Trainee trainee = traineeDao.findByUsername(username);
        boolean match = trainee != null && Arrays.equals(trainee.getPassword(), password);

        log.debug("Password match for {}: {}", username, match);
        return match;
    }

    @Override
    public Trainee changePassword(String username, char[] newPassword) {
        log.debug("Changing password for Trainee: {}", username);

        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee != null) {
            trainee.setPassword(newPassword);
            traineeDao.update(trainee);
            log.info("Password changed for Trainee: {}", username);
        } else {
            log.debug("Password change skipped – Trainee not found: {}", username);
        }

        return trainee;
    }

    @Override
    public Trainee setActiveStatus(String username, boolean active) {
        log.debug("Setting active status for Trainee: {}, active={}", username, active);

        Trainee trainee = traineeDao.findByUsername(username);
        if (trainee != null) {
            trainee.setActive(active);
            traineeDao.update(trainee);
            log.info("Active status updated for Trainee: {}", username);
        } else {
            log.debug("Active status update skipped – Trainee not found: {}", username);
        }

        return trainee;
    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.debug("Fetching all Trainees");

        List<Trainee> trainees = traineeDao.findAll();

        log.info("Total Trainees found: {}", trainees.size());
        return trainees;
    }

}
