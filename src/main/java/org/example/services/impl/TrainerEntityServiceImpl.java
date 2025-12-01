package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingTypeRepo;
import org.example.services.TrainerEntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TrainerEntityServiceImpl implements TrainerEntityService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeRepo trainingTypeRepo;

    @Override
    public Trainer createTrainer(Trainer trainer){
        log.info("Creating trainer with username: {}", trainer.getUsername());
        TrainerEntity trainerEntity = trainerMapper.toTrainerEntity(trainer);
        trainerRepo.save(trainerEntity);
        log.info("Trainer created successfully: {}", trainer.getUsername());
        return trainerMapper.toTrainerModel(trainerEntity);
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username){
        log.debug("Fetching trainer by username: {}", username);
        return trainerRepo.findByUsername(username)
                .map(t -> {
                    log.debug("Trainer found: {}", username);
                    return trainerMapper.toTrainerModel(t);
                });
    }

    @Override
    public Trainer updateTrainer(String username, Trainer updatedTrainer) {
        log.info("Updating trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        if (updatedTrainer.getFirstName() != null) {
            trainerEntity.getUserEntity().setFirstName(updatedTrainer.getFirstName());
        }
        if (updatedTrainer.getLastName() != null) {
            trainerEntity.getUserEntity().setLastName(updatedTrainer.getLastName());
        }

        if (updatedTrainer.getSpecialization() != null) {
            TrainingTypeEntity type = trainingTypeRepo
                    .findByName(updatedTrainer.getSpecialization())
                    .orElseThrow(() -> {
                        log.error("Training type not found: {}", updatedTrainer.getSpecialization());
                        return new RuntimeException("Invalid training specialization: " + updatedTrainer.getSpecialization());
                    });
            trainerEntity.setSpecialization(type);
            log.debug("Updated specialization for {} to {}", username, type.getTrainingTypeName());
        }

        TrainerEntity saved = trainerRepo.save(trainerEntity);
        log.info("Trainer updated successfully: {}", username);

        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkCredentials(String username, char[] password) {
        log.debug("Checking credentials for username: {}", username);
        boolean valid = trainerRepo.findByUsername(username)
                .map(t -> java.util.Arrays.equals(t.getUserEntity().getPassword(), password))
                .orElse(false);

        log.debug("Credentials valid for {}: {}", username, valid);
        return valid;
    }

    @Override
    public Trainer changePassword(String username, char[] newPassword) {
        log.info("Changing password for trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for password change: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        trainerEntity.getUserEntity().setPassword(newPassword);
        TrainerEntity saved = trainerRepo.save(trainerEntity);

        log.info("Password changed successfully for trainer: {}", username);
        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    public Trainer activateTrainer(String username) {
        log.info("Activating trainer: {}", username);

        TrainerEntity trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for activation: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        trainer.getUserEntity().setIsActive(true);
        Trainer activated = trainerMapper.toTrainerModel(trainerRepo.save(trainer));

        log.info("Trainer activated: {}", username);
        return activated;
    }

    @Override
    public Trainer deactivateTrainer(String username) {
        log.info("Deactivating trainer: {}", username);

        TrainerEntity trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for deactivation: {}", username);
                    return new RuntimeException("Trainer not found");
                });

        trainer.getUserEntity().setIsActive(false);
        Trainer deactivated = trainerMapper.toTrainerModel(trainerRepo.save(trainer));

        log.info("Trainer deactivated: {}", username);
        return deactivated;
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
