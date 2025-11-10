package org.example.service;

import org.example.model.Trainer;

import java.util.List;

public interface TrainerService {

    void createTrainer(Trainer trainer);

    void updateTrainer(Trainer trainer);

    Trainer getTrainer(String username);

    List<Trainer> listAll();
}
