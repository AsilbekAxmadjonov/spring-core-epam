package org.example.dao;

import org.example.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    private static final Logger logger = LoggerFactory.getLogger(TraineeDaoImpl.class);
    private final Map<String, Trainee> traineeStorage;

    public TraineeDaoImpl(@Qualifier("traineeStorage") Map<String, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public void save(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        logger.info("Saved new Trainee: {}", trainee.getUsername());
    }

    @Override
    public void update(Trainee trainee) {
        traineeStorage.put(trainee.getUsername(), trainee);
        logger.info("Updated Trainee: {}", trainee.getUsername());
    }

    @Override
    public void delete(Trainee trainee) {
        if (trainee == null || trainee.getUsername() == null) {
            logger.warn("Attempted to delete null Trainee or Trainee without username");
            return;
        }

        if (traineeStorage.containsKey(trainee.getUsername())) {
            traineeStorage.remove(trainee.getUsername());
            logger.info("Deleted Trainee: {}", trainee.getUsername());
        } else {
            logger.warn("Attempted to delete non-existent Trainee: {}", trainee.getUsername());
        }
    }


    @Override
    public Trainee findByUsername(String username) {
        logger.debug("Finding Trainee by username: {}", username);
        return traineeStorage.get(username);
    }

    @Override
    public List<Trainee> findAll() {
        logger.info("Fetching all Trainees. Total count: {}", traineeStorage.size());
        return traineeStorage.values().stream().toList();
    }
}
