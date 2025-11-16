
package org.example.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.service.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TrainingServiceImpl implements TrainingService {

    private TrainingDao trainingDao; // cannot be final with setter injection

    // Setter-based injection
    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public void createTraining(Training training) {
        log.info("Creating new Training: {}", training.getTrainingName());
        trainingDao.save(training);
    }

    @Override
    public Training getTraining(String name) {
        log.debug("Getting Training by name: {}", name);
        return trainingDao.findByName(name);
    }

    @Override
    public List<Training> listAll() {
        log.info("Listing all Trainings");
        return trainingDao.findAll();
    }
}
