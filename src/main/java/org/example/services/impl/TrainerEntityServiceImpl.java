package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.exception.UserNotFoundException;
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
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        trainerMapper.updateEntity(updatedTrainer, trainerEntity);

        TrainerEntity saved = trainerRepo.save(trainerEntity);
        log.info("Trainer updated successfully: {}", username);

        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean passwordMatches(String username, char[] password) {
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
                    log.error("Trainer not found for update: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        trainerEntity.getUserEntity().setPassword(newPassword);
        TrainerEntity saved = trainerRepo.save(trainerEntity);

        log.info("Password changed successfully for trainer: {}", username);
        return trainerMapper.toTrainerModel(saved);
    }

    @Override
    public Trainer setActiveStatus(String username, boolean active) {
        TrainerEntity trainer = trainerRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainer not found: " + username));

        trainer.getUserEntity().setIsActive(active);
        TrainerEntity saved = trainerRepo.save(trainer);

        log.info("Trainer {} set active={}", username, active);
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
