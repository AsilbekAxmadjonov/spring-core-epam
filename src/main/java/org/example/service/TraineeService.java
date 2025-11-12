package org.example.service;

import org.example.model.Trainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    void createTrainee(Trainee trainee);

    void updateTrainee(Trainee trainee);

    void deleteTrainee(Trainee trainee);

    Trainee getTrainee(String username);

    List<Trainee> listAll();
}
