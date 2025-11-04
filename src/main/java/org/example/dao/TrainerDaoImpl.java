package org.example.dao;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainerDaoImpl.class);
    private final Map<String, Trainer> trainerStorage;

    public TrainerDaoImpl(@Qualifier("trainerStorage") Map<String, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public void save(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        logger.info("Saved Trainer: {}", trainer.getUsername());
    }

    @Override
    public void update(Trainer trainer) {
        trainerStorage.put(trainer.getUsername(), trainer);
        logger.info("Updated Trainer: {}", trainer.getUsername());
    }

    @Override
    public Trainer findByUsername(String username) {
        logger.debug("Finding Trainer by username: {}", username);
        return trainerStorage.get(username);
    }

    @Override
    public List<Trainer> findAll() {
        logger.info("Fetching all Trainers. Total count: {}", trainerStorage.size());
        return trainerStorage.values().stream().toList();
    }
}
