package org.example.dao;

import org.example.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainerDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDao.class);
    private final Map<String, Trainer> trainerStorage;

    public TrainerDao(@Qualifier("trainerStorage") Map<String, Trainer> trainerStorage){
        this.trainerStorage = trainerStorage;
    }

    public void save(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        logger.info("Saved Trainer: ", trainer.getUsername());
    }

    public void update(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        logger.info("Updated Trainer: ", trainer.getUsername());
    }

    public Trainer findByUsername(String username) {
        logger.debug("Finding Trainer by username: ", username);
        return (Trainer) trainerStorage.get(username);
    }

    public List<Trainer> findAll() {
        logger.info("Fetching all Trainers. Total count of Trainers: ", trainerStorage.size());
        return trainerStorage.values()
                .stream()
                .map(o -> (Trainer) o)
                .toList();
    }
}
