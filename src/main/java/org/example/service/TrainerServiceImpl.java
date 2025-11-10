package org.example.service;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerServiceImpl implements TrainerService {

    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerDao trainerDao; // cannot be final with setter injection

    // Setter-based injection
    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Override
    public void createTrainer(Trainer trainer) {
        logger.info("Creating new Trainer: {}", trainer.getUsername());
        trainerDao.save(trainer);
    }

    @Override
    public void updateTrainer(Trainer trainer) {
        logger.info("Updating Trainer: {}", trainer.getUsername());
        trainerDao.update(trainer);
    }

    @Override
    public Trainer getTrainer(String username) {
        logger.info("Getting Trainer with username: {}", username);
        return trainerDao.findByUsername(username);
    }

    @Override
    public List<Trainer> listAll() {
        logger.info("Listing all Trainers...");
        return trainerDao.findAll();
    }
}
