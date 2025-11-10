package org.example.service;

import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingServiceImpl implements TrainingService {

    private static final Logger logger = LoggerFactory.getLogger(TrainingServiceImpl.class);

    private TrainingDao trainingDao; // cannot be final with setter injection

    // Setter-based injection
    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public void createTraining(Training training) {
        logger.info("Creating new Training: {}", training.getTrainingName());
        trainingDao.save(training);
    }

    @Override
    public Training getTraining(String name) {
        logger.debug("Getting Training by name: {}", name);
        return trainingDao.findByName(name);
    }

    @Override
    public List<Training> listAll() {
        logger.info("Listing all Trainings");
        return trainingDao.findAll();
    }
}
