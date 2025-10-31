package org.example.service;

import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainingService {

    @Autowired
    private final TrainingDao trainingDao;


    @Autowired
    public TrainingService(TrainingDao trainingDao){
        this.trainingDao = trainingDao;
    }

    public void createTraining(Training training){
        trainingDao.save(training);
    }

    public Training getTraining(String name){
        return trainingDao.findByName(name);
    }

    public List<Training> listAll(){
        return trainingDao.findAll();
    }
}
