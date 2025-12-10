package org.example.services;

import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Optional<Trainer> getTrainerByUsername(String username, char[] password);

    Trainer updateTrainer(String username, char[] password, Trainer updatedTrainer);

    List<Trainer> getAllTrainers();
}
