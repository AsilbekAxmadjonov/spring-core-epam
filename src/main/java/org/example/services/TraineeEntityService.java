package org.example.services;

import org.example.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeEntityService {
    Trainee createTrainee(Trainee trainee);
    Optional<Trainee> getTraineeByUsername(String username);

    Trainee updateTrainee(String username, Trainee trainee);

    void deleteTraineeByUsername(String username);

    boolean passwordMatches(String username, char[] password);

    Trainee changePassword(String username, char[] newPassword);

    Trainee setActiveStatus(String username, boolean active);


    List<Trainee> getAllTrainees();

}
