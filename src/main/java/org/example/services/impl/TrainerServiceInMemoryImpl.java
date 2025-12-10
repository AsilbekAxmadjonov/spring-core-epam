package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.services.AuthenticationService;
import org.example.services.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
@Validated
public class TrainerServiceInMemoryImpl implements TrainerService {

    private TrainerDao trainerDao;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public Trainer createTrainer(@Valid Trainer trainer) {
        log.info("Creating new Trainer: {}", trainer.getUsername());
        trainerDao.save(trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Getting Trainer by username: {}", username);
        Trainer trainer = trainerDao.findByUsername(username);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer updateTrainer(String username, char[] password, @Valid Trainer trainer) {
        authenticationService.authenticate(username, password);

        log.info("Updating Trainer: {}", username);
        trainerDao.update(trainer);
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}