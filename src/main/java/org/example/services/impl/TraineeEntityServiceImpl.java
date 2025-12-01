package org.example.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.mapper.TraineeMapper;
import org.example.mapper.TrainingMapper;
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
    private final TrainingMapper trainingMapper;

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
                    return new RuntimeException("Trainee not found");
                });

        if (updatedTrainee.getDateOfBirth() != null) {
            traineeEntity.setDateOfBirth(updatedTrainee.getDateOfBirth());
        }
        if (updatedTrainee.getAddress() != null) {
            traineeEntity.setAddress(updatedTrainee.getAddress());
        }
        if (updatedTrainee.getFirstName() != null) {
            traineeEntity.getUserEntity().setFirstName(updatedTrainee.getFirstName());
        }
        if (updatedTrainee.getLastName() != null) {
            traineeEntity.getUserEntity().setLastName(updatedTrainee.getLastName());
        }

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
    public boolean checkCredentials(String username, char[] password) {
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
                    log.error("Trainee not found for password change: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        traineeEntity.getUserEntity().setPassword(newPassword);
        TraineeEntity saved = traineeRepo.save(traineeEntity);
        log.info("Password changed successfully for trainee: {}", username);
        return traineeMapper.toTraineeModel(saved);
    }

    @Override
    public Trainee activateTrainee(String username) {
        log.info("Activating trainee: {}", username);
        TraineeEntity trainee = traineeRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for activation: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        trainee.getUserEntity().setIsActive(true);
        Trainee activated = traineeMapper.toTraineeModel(traineeRepo.save(trainee));
        log.info("Trainee activated: {}", username);
        return activated;
    }

    @Override
    public Trainee deactivateTrainee(String username) {
        log.info("Deactivating trainee: {}", username);
        TraineeEntity trainee = traineeRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for deactivation: {}", username);
                    return new RuntimeException("Trainee not found");
                });

        trainee.getUserEntity().setIsActive(false);
        Trainee deactivated = traineeMapper.toTraineeModel(traineeRepo.save(trainee));
        log.info("Trainee deactivated: {}", username);
        return deactivated;
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
