package org.example.services.impl.inMemoryImpl;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.services.TrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@Validated
public class TrainingServiceInMemoryImpl implements TrainingService {

    private TrainingDao trainingDao;

    // Setter-based injection
    @Autowired
    public void setTrainingDao(TrainingDao trainingDao) {
        this.trainingDao = trainingDao;
    }

    @Override
    public void createTraining(@Valid Training training) {
        log.debug("Attempting to create new Training: {}", training.getTrainingName());

        trainingDao.save(training);

        log.info("Training created: {}", training.getTrainingName());
    }

    @Override
    public Training getTraining(String name) {
        log.debug("Fetching Training by name: {}", name);

        Training training = trainingDao.findByName(name);
        if (training == null) {
            log.warn("Training not found: {}", name);
            throw new NoSuchElementException("Training not found: " + name);
        }

        log.info("Training retrieved: {}", name);
        return training;
    }

    @Override
    public List<Training> listAll() {
        log.debug("Fetching all Trainings from in-memory store");

        List<Training> trainings = trainingDao.findAll();

        log.info("Fetched {} trainings", trainings.size());
        return trainings;
    }

    @Override
    public List<Training> getTraineeTrainings(String traineeUsername,
                                              LocalDate fromDate,
                                              LocalDate toDate,
                                              String trainerName,
                                              String trainingType) {
        log.debug("Attempted filtering trainee trainings in in-memory service (not supported)");
        throw new UnsupportedOperationException("Filtering not supported in in-memory implementation");
    }

    @Override
    public List<Training> getTrainerTrainings(String trainerUsername,
                                              LocalDate fromDate,
                                              LocalDate toDate,
                                              String traineeName) {
        log.debug("Attempted filtering trainer trainings in in-memory service (not supported)");
        throw new UnsupportedOperationException("Filtering not supported in in-memory implementation");
    }

    @Override
    public Training addTraining(@Valid Training training) {
        log.debug("Adding new Training: {}", training.getTrainingName());

        createTraining(training);

        log.info("Training added: {}", training.getTrainingName());
        return training;
    }

}
