package org.example.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.example.dao.ProfileSavableDao;
import org.example.dao.TraineeDao;
import org.example.dao.TrainerDao;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class ProfileSavableDaoImpl implements ProfileSavableDao {

    private TrainerDao trainerDao;
    private TraineeDao traineeDao;

    @Autowired
    public void setTrainerDao(TrainerDao trainerDao) {
        this.trainerDao = trainerDao;
    }

    @Autowired
    public void setTraineeDao(TraineeDao traineeDao) {
        this.traineeDao = traineeDao;
    }

    @Override
    public void save(User user) {
        if (user instanceof Trainer trainer) {
            trainerDao.save(trainer);
        } else if (user instanceof Trainee trainee) {
            traineeDao.save(trainee);
        } else {
            throw new IllegalArgumentException("Unsupported user type: " + user.getClass().getSimpleName());
        }
    }

    @Override
    public List<String> findAllUsernames() {
        List<String> usernames = new ArrayList<>();

        trainerDao.findAll().forEach(t -> usernames.add(t.getUsername()));
        traineeDao.findAll().forEach(t -> usernames.add(t.getUsername()));

        return usernames;
    }
}
