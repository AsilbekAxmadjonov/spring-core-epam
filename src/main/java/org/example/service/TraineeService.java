package org.example.service;

import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeService {

    @Autowired
    private final TraineeDao traineeDao;

    @Autowired
    public TraineeService(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    public void createTrainee(Trainee trainee){
        traineeDao.save(trainee);
    }

    public void updateTrainee(Trainee trainee){
        traineeDao.update(trainee);
    }

    public void delete(Trainee trainee) {
        if (trainee != null) {
            traineeDao.delete(trainee);
        }
    }
    public Trainee getTrainee(String username){
        return traineeDao.findByUsername(username);
    }

    public List<Trainee> listAll(){
        return traineeDao.findAll();
    }
}
