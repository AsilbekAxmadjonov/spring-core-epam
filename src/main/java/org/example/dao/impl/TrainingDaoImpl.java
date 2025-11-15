package org.example.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class TrainingDaoImpl implements TrainingDao {

    private Map<String, Training> trainingStorage;

    // Setter-based injection for trainingStorage
    @Autowired
    public void setTrainingStorage(@Qualifier("trainingStorage") Map<String, Training> trainingStorage) {
        this.trainingStorage = trainingStorage;
    }

    @Override
    public void save(Training training) {
        trainingStorage.put(training.getTrainingName(), training);
        log.info("Saved new Training: {}", training.getTrainingName());
    }

    @Override
    public Training findByName(String name) {
        log.debug("Finding Training by name: {}", name);
        return trainingStorage.get(name);
    }

    @Override
    public List<Training> findAll() {
        log.info("Fetching all Trainings. Total count: {}", trainingStorage.size());
        return trainingStorage.values().stream().toList();
    }
}
