package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.example.services.TraineeService;
import org.slf4j.MDC;
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
public class TraineeServiceDbImpl implements TraineeService {

    private final TraineeRepo traineeRepo;
    private final TraineeMapper traineeMapper;
    private final UserRepo userRepo;

    @Override
    public Trainee createTrainee(@Valid Trainee traineeModel) {
        MDC.put("operation", "createTrainee");
        MDC.put("username", traineeModel.getUsername());

        log.debug("Starting creation of trainee: {}", traineeModel.getUsername());

        UserEntity userEntity = userRepo.findByUsername(traineeModel.getUsername())
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with username: " + traineeModel.getUsername()));

        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setUserEntity(userEntity);
        traineeEntity.setDateOfBirth(traineeModel.getDateOfBirth());
        traineeEntity.setAddress(traineeModel.getAddress());

        TraineeEntity savedTrainee = traineeRepo.save(traineeEntity);
        log.info("Trainee created successfully: {}", traineeModel.getUsername());

        return traineeMapper.toTraineeModel(savedTrainee);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> getTraineeByUsername(String username) {
        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();

        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
            throw new SecurityException("Trainee not authenticated");
        }

        log.debug("Fetching trainee by username: {}", username);

        return traineeRepo.findByUsername(username)
                .map(traineeEntity -> {
                    log.debug("Trainee found: {}", username);
                    return traineeMapper.toTraineeModel(traineeEntity);
                });
    }

    @Override
    public Trainee updateTrainee(String username, Trainee updatedTrainee) {

        MDC.put("operation", "updateTrainee");
        MDC.put("username", username);

        String authenticated = AuthenticationContext.getAuthenticatedUser();

        if (authenticated == null || !authenticated.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

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
    public void deleteTraineeByUsername(String username) {

        MDC.put("operation", "deleteTrainee");
        MDC.put("username", username);

        String authenticated = AuthenticationContext.getAuthenticatedUser();

        if (authenticated == null || !authenticated.equals(username)) {
            throw new SecurityException("User not authenticated");
        }

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