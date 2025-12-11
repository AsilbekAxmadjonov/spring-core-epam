package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingTypeRepo;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.example.services.TrainerService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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
    private final UserRepo userRepo;  // Add UserRepo dependency

    @Override
    public Trainer createTrainer(@Valid Trainer trainer) {
        log.info("Creating trainer with username: {}", trainer.getUsername());

        // CRITICAL: Fetch the existing user from database instead of creating new one
        UserEntity userEntity = userRepo.findByUsername(trainer.getUsername())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + trainer.getUsername()));

        TrainerEntity trainerEntity = new TrainerEntity();
        trainerEntity.setUserEntity(userEntity);  // Set the persisted user

        // Find and set the existing training type
        if (trainer.getSpecialization() != null) {
            TrainingTypeEntity trainingType = trainingTypeRepo
                    .findByTrainingTypeName(trainer.getSpecialization())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid training type: " + trainer.getSpecialization() +
                                    ". Please use one of the predefined training types."));

            trainerEntity.setSpecialization(trainingType);
        }

        TrainerEntity savedTrainer = trainerRepo.save(trainerEntity);
        log.info("Trainer created successfully: {}", trainer.getUsername());

        return trainerMapper.toTrainerModel(savedTrainer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainer> getTrainerByUsername(String username) {
        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

        log.debug("Fetching trainer by username: {}", username);

        return trainerRepo.findByUsername(username)
                .map(trainerEntity -> {
                    log.debug("Trainer found: {}", username);
                    return trainerMapper.toTrainerModel(trainerEntity);
                });
    }

    @Override
    public Trainer updateTrainer(String username, @Valid Trainer updatedTrainer) {
        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

        log.info("Updating trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        trainerMapper.updateEntity(updatedTrainer, trainerEntity);

        // Update training type if specialization changed
        if (updatedTrainer.getSpecialization() != null) {
            TrainingTypeEntity trainingType = trainingTypeRepo
                    .findByTrainingTypeName(updatedTrainer.getSpecialization())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Invalid training type: " + updatedTrainer.getSpecialization()));

            trainerEntity.setSpecialization(trainingType);
        }

        TrainerEntity saved = trainerRepo.save(trainerEntity);
        log.info("Trainer updated successfully: {}", username);

        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trainer> getAllTrainers() {
        log.info("Fetching all trainers");
        List<TrainerEntity> trainerEntities = trainerRepo.findAll();
        List<Trainer> trainers = trainerMapper.toTrainerModels(trainerEntities);

        log.info("Total trainers fetched: {}", trainers.size());
        return trainers;
    }
}