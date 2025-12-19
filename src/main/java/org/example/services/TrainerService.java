package org.example.services;

import jakarta.validation.Valid;
import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(@Valid Trainer trainer);

    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(String username, @Valid Trainer updatedTrainer);

    List<Trainer> getAllTrainers();
}
