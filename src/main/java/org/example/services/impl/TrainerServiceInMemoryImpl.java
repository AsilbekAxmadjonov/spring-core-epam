package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.services.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Service
public class TrainerServiceInMemoryImpl implements TrainerService {

    private TrainerDao trainerDao;

    // Setter-based injection
    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public Trainer createTrainer(Trainer trainer) {
        log.info("Creating new Trainer: {}", trainer.getUsername());
        trainerDao.save(trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username) {
        log.debug("Getting Trainer by username: {}", username);
        Trainer trainer = trainerDao.findByUsername(username);
        return Optional.ofNullable(trainer);
    }

    @Override
    public Trainer updateTrainer(String username, Trainer trainer) {
        log.info("Updating Trainee: {}", username);
        trainerDao.update(trainer);
        return trainer;
    }

    @Override
    public boolean passwordMatches(String username, char[] password) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) return false;
        return Arrays.equals(trainer.getPassword(), password);
    }

    @Override
    public Trainer changePassword(String username, char[] newPassword) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            log.error("Trainer not found for password change: {}", username);
            throw new NoSuchElementException("Trainer not found: " + username);
        }
        trainer.setPassword(newPassword);
        trainerDao.update(trainer);
        log.info("Password changed for trainer: {}", username);
        return trainer;
    }

    @Override
    public Trainer setActiveStatus(String username, boolean active) {
        Trainer trainer = trainerDao.findByUsername(username);
        if (trainer == null) {
            log.error("Trainer not found for status change: {}", username);
            throw new NoSuchElementException("Trainer not found: " + username);
        }
        trainer.setActive(active);
        trainerDao.update(trainer);
        log.info("Trainer {} set active={}", username, active);
        return trainer;
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }
}
