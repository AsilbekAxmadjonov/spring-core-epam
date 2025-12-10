package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.services.AuthenticationService;
import org.example.services.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class TraineeServiceInMemoryImpl implements TraineeService {

    private TraineeDao traineeDao;
    private AuthenticationService authenticationService; // ADD THIS

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Trainee createTrainee(@Valid Trainee trainee) {
        log.debug("Starting creation of Trainee: {}", trainee.getUsername());

        traineeDao.save(trainee);

        log.info("Trainee created successfully: {}", trainee.getUsername());
        return trainee;
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

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
    public Trainee updateTrainee(String username, char[] password, @Valid Trainee updatedTrainee) {
        authenticationService.authenticate(username, password);

        log.debug("Starting update for Trainee: {}", username);

        traineeDao.update(updatedTrainee);

        log.info("Trainee updated successfully: {}", username);
        return updatedTrainee;
    }

    @Override
    public void deleteTraineeByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

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
    public List<Trainee> getAllTrainees() {
        log.debug("Fetching all Trainees");

        List<Trainee> trainees = traineeDao.findAll();

        log.info("Total Trainees found: {}", trainees.size());
        return trainees;
    }

}