package org.example.services.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class TrainingServiceInMemoryImpl implements TrainingService {

    private TrainingDao trainingDao;

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
        Training training = trainingDao.findByName(name);
        if (training == null) {
            log.error("Training not found: {}", name);
            throw new NoSuchElementException("Training not found: " + name);
        }
        return training;
    }

    @Override
    public List<Training> listAll() {
        log.info("Listing all Trainings");
        return trainingDao.findAll();
    }

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername, LocalDate fromDate, LocalDate toDate,
                                              String trainerName, String trainingType) {
        throw new UnsupportedOperationException("Filtering not supported in in-memory implementation");
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername, LocalDate fromDate, LocalDate toDate,
                                              String traineeName) {
        throw new UnsupportedOperationException("Filtering not supported in in-memory implementation");
    }

    @Override
    public Training addTraining(Training training) {
        createTraining(training); // reuse simple save
        return training;
    }
}
