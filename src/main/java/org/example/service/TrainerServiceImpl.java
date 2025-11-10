package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.util.ProfileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainerServiceImpl implements TrainerService {

    private TrainerDao trainerDao; // cannot be final with setter injection

    // Setter-based injection
    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public void createTrainerProfile(String firstName, String lastName, String specialization) {
        List<String> existingUsernames = trainerDao.findAll().stream()
                .map(Trainer::getUsername)
                .toList();

        String username = ProfileGenerator.generateUsername(firstName, lastName, existingUsernames);
        String password = ProfileGenerator.generateRandomPassword();

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
        trainer.setUsername(username);
        trainer.setPassword(password);
        trainer.setSpecialization(specialization);

        trainerDao.save(trainer);

        log.info("Created new Trainer profile: {}", username);
    }

    @Override
    public void createTrainer(Trainer trainer) {
        log.info("Creating new Trainer: {}", trainer.getUsername());
        trainerDao.save(trainer);
    }

    @Override
    public void updateTrainer(Trainer trainer) {
        log.info("Updating Trainer: {}", trainer.getUsername());
        trainerDao.update(trainer);
    }

    @Override
    public Trainer getTrainer(String username) {
        log.debug("Getting Trainer with username: {}", username);
        return trainerDao.findByUsername(username);
    }

    @Override
    public List<Trainer> listAll() {
        log.info("Listing all Trainers...");
        return trainerDao.findAll();
    }
}
