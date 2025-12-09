package org.example.services;

import org.example.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerService {

    Trainer createTrainer(Trainer trainer);

    Optional<Trainer> getTrainerByUsername(String username);

    Trainer updateTrainer(String username, Trainer trainer);

    boolean passwordMatches(String username, char[] password);

    Trainer changePassword(String username, char[] newPassword);

    Trainer setActiveStatus(String username, boolean active);

    List<Trainer> getAllTrainers();
}
