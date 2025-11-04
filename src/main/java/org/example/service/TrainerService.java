package org.example.service;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerService.class);

    @Autowired
    public final TrainerDao trainerDao;

    @Autowired
    public TrainerService(TrainerDao trainerDao){
        this.trainerDao = trainerDao;
    }

    public void createTrainerProfile(String firstName, String lastName, String specialization) {
        List<String> existingUsernames = trainerDao.findAll().stream()
                .map(Trainer::getUsername)
                .toList();

//        String username = ProfileGenerator.generateUsername(firstName, lastName, existingUsernames);
//        String password = ProfileGenerator.generateRandomPassword();

        Trainer trainer = new Trainer();
        trainer.setFirstName(firstName);
        trainer.setLastName(lastName);
//        trainer.setUsername(username);
//        trainer.setPassword(password);
        trainer.setSpecialization(specialization);

        trainerDao.save(trainer);
    }

    public void createTrainer(Trainer trainer){
        logger.info("Creating new Trainer: {}", trainer.getUsername());
        trainerDao.save(trainer);
    }

    public void updateTrainer(Trainer trainer){
        logger.info("Updating trainer: {}", trainer.getUsername());
        trainerDao.update(trainer);
    }

    public Trainer getTrainer(String username){
        logger.info("Getting trainer with username: {}", username);
        return trainerDao.findByUsername(username);
    }

    public List<Trainer> listAll(){
        logger.info("Listing all Trainer...");
        return trainerDao.findAll();
    }
}
