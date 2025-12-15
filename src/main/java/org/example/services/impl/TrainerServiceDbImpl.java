package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TrainerEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.services.TrainerService;
import org.slf4j.MDC;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class TrainerServiceDbImpl implements TrainerService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;

    @Override
    public Trainer createTrainer(Trainer trainer){

        MDC.put("operation", "createTrainer");
        MDC.put("username", trainer.getUsername());

        log.info("Creating trainer with username: {}", trainer.getUsername());
        TrainerEntity trainerEntity = trainerMapper.toTrainerEntity(trainer);
        trainerRepo.save(trainerEntity);
        log.info("Trainer created successfully: {}", trainer.getUsername());
        return trainerMapper.toTrainerModel(trainerEntity);
    }

    @Override
    public Optional<Trainer> getTrainerByUsername(String username) {
        log.debug("Fetching trainer by username: {}", username);

        return trainerRepo.findByUsername(username)
                .map(trainerEntity -> {
                    log.debug("Trainer found: {}", username);
                    return trainerMapper.toTrainerModel(trainerEntity);
                });
    }


    @Override
    public Trainer updateTrainer(String username, Trainer updatedTrainer) {

        MDC.put("operation", "updateTrainer");
        MDC.put("username", username);

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
                .map(trainerEntity -> Arrays.equals(trainerEntity.getUserEntity().getPassword(), password))
                .orElse(false);

        log.debug("Credentials valid for {}: {}", username, valid);
        return valid;
    }

    @Override
    public Trainer changePassword(String username, char[] newPassword) {

        MDC.put("operation", "changePassword");
        MDC.put("username", username);

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
