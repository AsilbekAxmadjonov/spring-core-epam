package org.example.dao;

import org.example.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TraineeDao {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDao.class);
    private final Map<String, Trainee> traineeStorage;

    public TraineeDao(@Qualifier("traineeStorage") Map<String, Trainee> traineeStorage){
        this.traineeStorage = traineeStorage;
    }

    public void save(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        logger.info("Saved new Trainee: ", trainee.getUsername());
    }

    public void update(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        logger.info("Updating Trainee: ", trainee.getUsername());
    }

    public Trainee findByUsername(String username) {
        logger.debug("Finder Trainee by username: ", username);
        return (Trainee) traineeStorage.get(username);
    }

    public List<Trainee> findAll() {
        logger.info("Fetching all Trainees. Total count: ", traineeStorage.size());
        return traineeStorage.values()
                .stream()
                .map(o -> (Trainee) o)
                .toList();
    }
}
