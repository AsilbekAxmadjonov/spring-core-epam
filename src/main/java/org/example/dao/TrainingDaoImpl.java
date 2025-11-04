package org.example.dao;

import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    private static final Logger logger = LoggerFactory.getLogger(TrainingDaoImpl.class);
    private final Map<String, Training> trainingStorage;

    public TrainingDaoImpl(@Qualifier("trainingStorage") Map<String, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void save(Training training) {
        logger.info("Saved new Training: {}", training.getTrainingName());
        trainingStorage.put(training.getTrainingName(), training);
    }

    @Override
    public Training findByName(String name) {
        logger.debug("Finding Training by name: {}", name);
        return trainingStorage.get(name);
    }

    @Override
    public List<Training> findAll() {
        logger.info("Fetching all Trainings. Total count: {}", trainingStorage.size());
        return trainingStorage.values().stream().toList();
    }
}
