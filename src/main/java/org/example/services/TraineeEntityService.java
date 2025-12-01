package org.example.services;

import org.example.model.Trainee;
import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TraineeEntityService {
    Trainee createTrainee(Trainee trainee);
    Optional<Trainee> getTraineeByUsername(String username);

    Trainee updateTrainee(String username, Trainee trainee);

    void deleteTraineeByUsername(String username);
    boolean checkCredentials(String username, char[] password);
    Trainee changePassword(String username, char[] newPassword);

    Trainee activateTrainee(String username);
    Trainee deactivateTrainee(String username);

    List<Trainee> getAllTrainees();

}
