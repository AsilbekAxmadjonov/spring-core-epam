package org.example.service;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrainerService {

    @Autowired
    public final TrainerDao trainerDao;

    @Autowired
    public TrainerService(TrainerDao trainerDao){
        this.trainerDao = trainerDao;
    }


    public void createTrainer(Trainer trainer){
        trainerDao.save(trainer);
    }

    public void updateTrainer(Trainer trainer){
        trainerDao.update(trainer);
    }

    public Trainer getTrainer(String username){
        return trainerDao.findByUsername(username);
    }

    public List<Trainer> listAll(){
        return trainerDao.findAll();
    }
}
