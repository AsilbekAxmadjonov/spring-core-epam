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
import org.example.services.TokenService;
import org.example.services.TrainerService;
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
public class TrainerServiceDbImpl implements TrainerService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;
    private final TrainingTypeRepo trainingTypeRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserRepo userRepo;
    private final TokenService tokenService;

    @Override
    @Transactional
    public Trainer createTrainer(@Valid Trainer trainer) {

        MDC.put("operation", "createTrainer");
        MDC.put("username", trainer.getUsername());

        log.info("Creating trainer with username: {}", trainer.getUsername());

        Optional<UserEntity> existingUser = userRepo.findByUsername(trainer.getUsername());

        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("User with username " + trainer.getUsername() + " already exists");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(trainer.getUsername());
        userEntity.setFirstName(trainer.getFirstName());
        userEntity.setLastName(trainer.getLastName());

        String passwordString = new String(trainer.getPassword());
        String encodedPassword = passwordEncoder.encode(passwordString);
        userEntity.setPassword(encodedPassword.toCharArray());

        userEntity.setIsActive(true);

        UserEntity savedUser = userRepo.save(userEntity);
        log.info("User created successfully: {}", trainer.getUsername());

        TrainerEntity trainerEntity = new TrainerEntity();
        trainerEntity.setUserEntity(savedUser);

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

        String token = tokenService.generateToken(savedUser.getUsername());
        log.info("JWT token generated for trainer: {}", trainer.getUsername());

        Trainer trainerModel = trainerMapper.toTrainerModel(savedTrainer);
        trainerModel.setToken(token);

        return trainerModel;
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

        MDC.put("operation", "updateTrainer");
        MDC.put("username", username);

//        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
//            throw new SecurityException("User not authenticated");
//        }

        log.info("Updating trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        trainerMapper.updateEntity(updatedTrainer, trainerEntity);

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
    public List<Trainer> getAllTrainers() {
        log.info("Fetching all trainers");
        List<TrainerEntity> trainerEntities = trainerRepo.findAll();
        List<Trainer> trainers = trainerMapper.toTrainerModels(trainerEntities);

        log.info("Total trainers fetched: {}", trainers.size());
        return trainers;
    }
}