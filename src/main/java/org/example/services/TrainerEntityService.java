package org.example.services;

import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerEntityService {

    Trainer createTrainer(Trainer trainer);
    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(String username, Trainer trainer);

    boolean checkCredentials(String username, char[] password);

    Trainer changePassword(String username, char[] newPassword);

    Trainer activateTrainer(String username);
    Trainer deactivateTrainer(String username);

    List<Trainer> getAllTrainers();
}
