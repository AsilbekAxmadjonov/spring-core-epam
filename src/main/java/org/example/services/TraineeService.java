package org.example.services;

import org.example.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);

    Optional<Trainee> getTraineeByUsername(String username, char[] password);

    Trainee updateTrainee(String username, char[] password, Trainee trainee);

    void deleteTraineeByUsername(String username, char[] password);

    List<Trainee> getAllTrainees();

}
