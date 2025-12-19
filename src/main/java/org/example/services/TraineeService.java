package org.example.services;

import jakarta.validation.Valid;
import org.example.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeService {
    Trainee createTrainee(@Valid Trainee trainee);

    Optional<Trainee> getTraineeByUsername(String username);

    Trainee updateTrainee(String username, Trainee trainee);

    void deleteTraineeByUsername(String username);

    List<Trainee> getAllTrainees();

}
