package org.example.services.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.example.services.AuthenticationService;
import org.example.services.TraineeService;
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
public class TraineeServiceDbImpl implements TraineeService {

    private final TraineeRepo traineeRepo;
    private final TraineeMapper traineeMapper;
    private final AuthenticationService authenticationService;

    @Override
    public Trainee createTrainee(@Valid Trainee traineeModel) {
        log.debug("Starting creation of trainee: {}", traineeModel.getUsername());

        TraineeEntity traineeEntity = traineeMapper.toTraineeEntity(traineeModel);
        traineeRepo.save(traineeEntity);

        log.info("Trainee created successfully: {}", traineeModel.getUsername());
        return traineeMapper.toTraineeModel(traineeEntity);
    }

    @Override
    public Optional<Trainee> getTraineeByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Fetching trainee by username: {}", username);

        return traineeRepo.findByUsername(username)
                .map(traineeEntity -> {
                    log.debug("Trainee found: {}", username);
                    return traineeMapper.toTraineeModel(traineeEntity);
                });
    }

    @Override
    public Trainee updateTrainee(String username, char[] password, Trainee updatedTrainee) {
        authenticationService.authenticate(username, password);

        log.debug("Starting update for trainee: {}", username);

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
    public void deleteTraineeByUsername(String username, char[] password) {
        authenticationService.authenticate(username, password);

        log.debug("Attempting to delete trainee: {}", username);

        boolean exists = traineeRepo.findByUsername(username).isPresent();
        if (exists) {
            traineeRepo.deleteByUsername(username);
            log.info("Trainee deleted successfully: {}", username);
        } else {
            log.warn("Trainee not found for deletion: {}", username);
        }
    }

    @Override
    public List<Trainee> getAllTrainees() {
        log.debug("Fetching all trainees");

        List<TraineeEntity> traineeEntities = traineeRepo.findAll();
        List<Trainee> trainees = traineeMapper.toTraineeModels(traineeEntities);

        log.info("Total trainees fetched: {}", trainees.size());
        return trainees;
    }

}
