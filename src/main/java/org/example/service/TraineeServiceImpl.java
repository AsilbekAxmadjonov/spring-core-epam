package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.util.ProfileGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class TraineeServiceImpl implements TraineeService {

    private TraineeDao traineeDao;

    // Setter-based injection
    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void createTrainee(Trainee trainee) {
        log.info("Creating new Trainee: {}", trainee.getUsername());
        traineeDao.save(trainee);
    }

    @Override
    public void updateTrainee(Trainee trainee) {
        log.info("Updating Trainee: {}", trainee.getUsername());
        traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Trainee trainee) {
        log.info("Deleted Trainee: {}", trainee.getUsername());
        traineeDao.delete(trainee);
    }

    @Override
    public Trainee getTrainee(String username) {
        log.debug("Getting Trainee by username: {}", username);
        return traineeDao.findByUsername(username);
    }

    @Override
    public List<Trainee> listAll() {
        log.info("Listing all Trainees");
        return traineeDao.findAll();
    }
}
