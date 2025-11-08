package org.example.service;

import org.example.model.Trainee;

import java.time.LocalDate;
import java.util.List;

public interface TraineeService {

    void createTraineeProfile(String firstName, String lastName, LocalDate dateOfBirth, String address);

    void createTrainee(Trainee trainee);

    void updateTrainee(Trainee trainee);

    void deleteTrainee(Trainee trainee);

    Trainee getTrainee(String username);

    List<Trainee> listAll();
}
