package org.example.service;

import org.example.model.Training;

import java.util.List;

public interface TrainingService {

    void createTraining(Training training);

    Training getTraining(String name);

    List<Training> listAll();
}
