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
import org.example.services.TokenService;
import org.example.services.TraineeService;
import org.slf4j.MDC;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public Trainee createTrainee(@Valid Trainee trainee) {

        MDC.put("operation", "createTrainee");
        MDC.put("username", trainee.getUsername());

        log.info("Creating trainee with username: {}", trainee.getUsername());

        // Check if user already exists
        Optional<UserEntity> existingUser = userRepo.findByUsername(trainee.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with username " + trainee.getUsername() + " already exists");
        }

        // Create the User first
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(trainee.getUsername());
        userEntity.setFirstName(trainee.getFirstName());
        userEntity.setLastName(trainee.getLastName());

        // Convert char[] to String, encode, then convert back to char[]
        String passwordString = new String(trainee.getPassword());
        String encodedPassword = passwordEncoder.encode(passwordString);
        userEntity.setPassword(encodedPassword.toCharArray());

        userEntity.setIsActive(true);

        UserEntity savedUser = userRepo.save(userEntity);
        log.info("User created successfully: {}", trainee.getUsername());

        // Now create the Trainee
        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setUserEntity(savedUser);
        traineeEntity.setDateOfBirth(trainee.getDateOfBirth());
        traineeEntity.setAddress(trainee.getAddress());

        TraineeEntity savedTrainee = traineeRepo.save(traineeEntity);
        log.info("Trainee created successfully: {}", trainee.getUsername());

        // Generate JWT token using TokenService
        String token = tokenService.generateToken(savedUser.getUsername());
        log.info("JWT token generated for trainee: {}", trainee.getUsername());

        // Convert to model and add token
        Trainee traineeModel = traineeMapper.toTraineeModel(savedTrainee);
        traineeModel.setToken(token);

        return traineeModel;
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

//        String authenticated = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticated == null || !authenticated.equals(username)) {
//            throw new SecurityException("User not authenticated");
//        }

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

//        String authenticated = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticated == null || !authenticated.equals(username)) {
//            throw new SecurityException("User not authenticated");
//        }

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