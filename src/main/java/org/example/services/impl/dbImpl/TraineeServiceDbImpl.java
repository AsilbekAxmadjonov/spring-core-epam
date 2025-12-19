package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
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

import java.security.SecureRandom;
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

    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public Trainee createTrainee(Trainee trainee) {

        MDC.put("operation", "createTrainee");

        String baseUsername = trainee.getFirstName() + "." + trainee.getLastName();
        String username = generateUniqueUsername(baseUsername);

        String plainPassword = generateRandomPassword();

        trainee.setUsername(username);
        trainee.setPassword(plainPassword.toCharArray());

        MDC.put("username", username);
        log.info("Creating trainee with generated username: {}", username);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setFirstName(trainee.getFirstName());
        userEntity.setLastName(trainee.getLastName());

        String encodedPassword = passwordEncoder.encode(plainPassword);
        userEntity.setPassword(encodedPassword.toCharArray());
        userEntity.setIsActive(true);

        UserEntity savedUser = userRepo.save(userEntity);
        log.info("User created successfully: {}", username);

        TraineeEntity traineeEntity = new TraineeEntity();
        traineeEntity.setUserEntity(savedUser);
        traineeEntity.setDateOfBirth(trainee.getDateOfBirth());
        traineeEntity.setAddress(trainee.getAddress());

        TraineeEntity savedTrainee = traineeRepo.save(traineeEntity);
        log.info("Trainee created successfully: {}", username);

        String token = tokenService.generateToken(savedUser.getUsername());
        log.info("JWT token generated for trainee: {}", username);

        Trainee traineeModel = traineeMapper.toTraineeModel(savedTrainee);
        traineeModel.setToken(token);
        traineeModel.setPassword(plainPassword.toCharArray());

        return traineeModel;
    }

    private String generateUniqueUsername(String baseUsername) {
        String username = baseUsername;
        int counter = 1;

        while (userRepo.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

    private String generateRandomPassword() {
        return random.ints(PASSWORD_LENGTH, 0, 62)
                .map(i -> {
                    if (i < 26) return 'A' + i;        // A-Z
                    if (i < 52) return 'a' + (i - 26); // a-z
                    return '0' + (i - 52);              // 0-9
                })
                .collect(StringBuilder::new,
                        StringBuilder::appendCodePoint,
                        StringBuilder::append)
                .toString();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trainee> getTraineeByUsername(String username) {

        log.debug("Fetching trainee by username: {}", username);

        return traineeRepo.findByUsername(username)
                .map(traineeEntity -> {
                    log.debug("Trainee found: {}", username);
                    return traineeMapper.toTraineeModel(traineeEntity);
                });
    }

    @Override
    @Transactional
    public Trainee updateTrainee(String username, Trainee updatedTrainee) {

        MDC.put("operation", "updateTrainee");
        MDC.put("username", username);

        log.debug("Starting update for trainee: {}", username);

        TraineeEntity traineeEntity = traineeRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainee not found for update: {}", username);
                    return new UserNotFoundException("Trainee not found with username: " + username);
                });

        // Check if name changed
        boolean nameChanged = !traineeEntity.getUserEntity().getFirstName().equals(updatedTrainee.getFirstName()) ||
                !traineeEntity.getUserEntity().getLastName().equals(updatedTrainee.getLastName());

        // Update the entity fields
        traineeMapper.updateEntity(updatedTrainee, traineeEntity);

        String newToken = null;

        // Regenerate username and token if name changed
        if (nameChanged) {
            String baseUsername = updatedTrainee.getFirstName() + "." + updatedTrainee.getLastName();
            String newUsername = generateUniqueUsername(baseUsername);
            traineeEntity.getUserEntity().setUsername(newUsername);

            // Generate new token with new username
            newToken = tokenService.generateToken(newUsername);

            log.info("Username updated from {} to {} with new token generated", username, newUsername);
        }

        TraineeEntity saved = traineeRepo.save(traineeEntity);
        String finalUsername = saved.getUserEntity().getUsername();

        log.info("Trainee updated successfully: {}", finalUsername);

        Trainee traineeModel = traineeMapper.toTraineeModel(saved);

        // Set new token if username changed
        if (newToken != null) {
            traineeModel.setToken(newToken);
            log.info("⚠️ Username changed! Client must use new username '{}' and new token for future requests", finalUsername);
        }

        return traineeModel;
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