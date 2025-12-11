package org.example.services;

import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(String username, Trainer updatedTrainer);

    List<Trainer> getAllTrainers();
}
