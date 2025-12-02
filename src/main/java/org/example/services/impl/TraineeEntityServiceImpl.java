package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.example.services.TraineeEntityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TraineeEntityServiceImpl implements TraineeEntityService {

    private final TraineeRepo traineeRepo;
    private final TraineeMapper traineeMapper;

    @Override
    public Trainee createTrainee(Trainee traineeModel) {
        log.info("Creating trainee with username: {}", traineeModel.getUsername());
        TraineeEntity traineeEntity = traineeMapper.toTraineeEntity(traineeModel);
        traineeRepo.save(traineeEntity);
        log.info("Trainee created successfully: {}", traineeModel.getUsername());
        return traineeMapper.toTraineeModel(traineeEntity);
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String username) {
        log.debug("Fetching trainee by username: {}", username);
        return traineeRepo.findByUsername(username)
                .map(t -> {
                    log.debug("Trainee found: {}", username);
                    return traineeMapper.toTraineeModel(t);
                });
    }


    @Override
    public Trainee updateTrainee(String username, Trainee updatedTrainee) {
        log.info("Updating trainee: {}", username);

        TraineeEntity traineeEntity = traineeRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for update: {}", username);
                    return new UserNotFoundException("Trainee not found with username: " + username);
                });

        traineeMapper.updateEntity(updatedTrainee, traineeEntity);

        TraineeEntity saved = traineeRepo.save(traineeEntity);
        log.info("Trainee updated successfully: {}", username);

        return traineeMapper.toTraineeModel(saved);
    }


    @Override
    @Transactional
    public void deleteTraineeByUsername(String username) {
        log.info("Deleting trainee by username: {}", username);

        boolean exists = traineeRepo.findByUsername(username).isPresent();
        if (exists) {
            traineeRepo.deleteByUsername(username);
            log.info("Trainee deleted successfully: {}", username);
        } else {
            log.warn("Trainee not found for deletion: {}", username);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean passwordMatches(String username, char[] password) {
        log.debug("Checking credentials for username: {}", username);
        boolean valid = traineeRepo.findByUsername(username)
                .map(t -> java.util.Arrays.equals(t.getUserEntity().getPassword(), password))
                .orElse(false);
        log.debug("Credentials valid for {}: {}", username, valid);
        return valid;
    }

    @Override
    public Trainee changePassword(String username, char[] newPassword) {
        log.info("Changing password for trainee: {}", username);
        TraineeEntity traineeEntity = traineeRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for update: {}", username);
                    return new UserNotFoundException("Trainee not found with username: " + username);
                });

        traineeEntity.getUserEntity().setPassword(newPassword);
        TraineeEntity saved = traineeRepo.save(traineeEntity);
        log.info("Password changed successfully for trainee: {}", username);
        return traineeMapper.toTraineeModel(saved);
    }

    @Override
    public Trainee setActiveStatus(String username, boolean active) {
        TraineeEntity trainee = traineeRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("Trainee not found: " + username));

        trainee.getUserEntity().setIsActive(active);
        TraineeEntity saved = traineeRepo.save(trainee);

        log.info("Trainee {} set active={}", username, active);
        return traineeMapper.toTraineeModel(saved);
    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.info("Fetching all trainees");
        List<TraineeEntity> traineeEntities = traineeRepo.findAll();
        List<Trainee> trainees = traineeMapper.toTraineeModels(traineeEntities);
        log.info("Total trainees fetched: {}", trainees.size());
        return trainees;
    }
}
