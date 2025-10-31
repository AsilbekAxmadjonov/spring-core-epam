package org.example.dao;

import org.example.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TrainingDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDao.class);
    private final Map<String, Training> trainingStorage;

    public TrainingDao(@Qualifier("trainingStorage") Map<String, Training> trainingStorage){
        this.trainingStorage = trainingStorage;
    }

    public void save(Training training) {
        logger.info("Saved new Training: ", training.getTrainingName());
        trainingStorage.put(training.getTrainingName(), training);
    }

    public Training findByName(String name) {
        logger.debug("Finding Training by name: ", name);
        return (Training) trainingStorage.get(name);
    }

    public List<Training> findAll() {
        logger.info("Fetching all Training. Total count: ", trainingStorage.size());
        return trainingStorage.values()
                .stream()
                .map(o -> (Training) o)
                .toList();
    }
}
