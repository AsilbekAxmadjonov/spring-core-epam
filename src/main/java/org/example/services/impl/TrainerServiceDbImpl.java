package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingTypeRepo;
import org.example.services.AuthenticationService;
import org.example.services.TrainerService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Primary
@Validated
@RequiredArgsConstructor
@Transactional
public class TrainerServiceDbImpl implements TrainerService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeRepo trainingTypeRepo;
    private final AuthenticationService authenticationService;

    @Override
    public Trainer createTrainer(@Valid Trainer trainer){
        log.info("Creating trainer with username: {}", trainer.getUsername());
        TrainerEntity trainerEntity = trainerMapper.toTrainerEntity(trainer);
        trainerRepo.save(trainerEntity);
        log.info("Trainer created successfully: {}", trainer.getUsername());
        return trainerMapper.toTrainerModel(trainerEntity);
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Fetching trainer by username: {}", username);

        return trainerRepo.findByUsername(username)
                .map(trainerEntity -> {
                    log.debug("Trainer found: {}", username);
                    return trainerMapper.toTrainerModel(trainerEntity);
                });
    }


    @Override
    public Trainer updateTrainer(String username, char[] password, @Valid Trainer updatedTrainer) {
        authenticationService.authenticate(username, password);

        log.info("Updating trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        trainerMapper.updateEntity(updatedTrainer, trainerEntity);

        TrainerEntity saved = trainerRepo.save(trainerEntity);
        log.info("Trainer updated successfully: {}", username);

        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    public List<Trainer> getAllTrainers() {
        log.info("Fetching all trainers");
        List<TrainerEntity> trainerEntities = trainerRepo.findAll();
        List<Trainer> trainers = trainerMapper.toTrainerModels(trainerEntities);

        log.info("Total trainers fetched: {}", trainers.size());
        return trainers;
    }


}