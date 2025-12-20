package org.example.services.impl.dbImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.persistance.entity.TrainerEntity;
import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.persistance.model.Trainer;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.example.persistance.repository.UserRepo;
import org.example.services.TokenService;
import org.example.services.TrainerService;
import org.example.services.UserService;
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
public class TrainerServiceDbImpl implements TrainerService {

    private final TrainerRepo trainerRepo;
    private final TrainerMapper trainerMapper;
    private final UserRepo userRepo;
    private final TrainingTypeRepo trainingTypeRepo;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final UserService userService;

    private static final int PASSWORD_LENGTH = 10;
    private static final SecureRandom random = new SecureRandom();

    @Override
    @Transactional
    public Trainer createTrainer(Trainer trainer) {

        MDC.put("operation", "createTrainer");

        String baseUsername = trainer.getFirstName() + "." + trainer.getLastName();
        String username = generateUniqueUsername(baseUsername);

        String plainPassword = generateRandomPassword();

        trainer.setUsername(username);
        trainer.setPassword(plainPassword.toCharArray());

        MDC.put("username", username);
        log.info("Creating trainer with generated username: {}", username);

        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setFirstName(trainer.getFirstName());
        userEntity.setLastName(trainer.getLastName());

        String encodedPassword = passwordEncoder.encode(plainPassword);
        userEntity.setPassword(encodedPassword.toCharArray());
        userEntity.setIsActive(true);

        UserEntity savedUser = userRepo.save(userEntity);
        log.info("User created successfully: {}", username);

        TrainingTypeEntity trainingType = trainingTypeRepo.findByTrainingTypeName(trainer.getSpecialization())
                .orElseThrow(() -> new IllegalArgumentException("Invalid specialization: " + trainer.getSpecialization()));

        TrainerEntity trainerEntity = new TrainerEntity();
        trainerEntity.setUserEntity(savedUser);
        trainerEntity.setSpecialization(trainingType);

        TrainerEntity savedTrainer = trainerRepo.save(trainerEntity);
        log.info("Trainer created successfully: {}", username);

        String token = tokenService.generateToken(savedUser.getUsername());
        log.info("JWT token generated for trainer: {}", username);

        Trainer trainerModel = trainerMapper.toTrainerModel(savedTrainer);
        trainerModel.setToken(token);
        trainerModel.setPassword(plainPassword.toCharArray());

        return trainerModel;
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
    public Optional<Trainer> getTrainerByUsername(String username) {
//        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticatedUser == null || !authenticatedUser.equals(username)) {
//            throw new SecurityException("Trainer not authenticated");
//        }

        log.debug("Fetching trainer by username: {}", username);

        return trainerRepo.findByUsername(username)
                .map(trainerEntity -> {
                    log.debug("Trainer found: {}", username);
                    return trainerMapper.toTrainerModel(trainerEntity);
                });
    }

    @Override
    @Transactional
    public Trainer updateTrainer(String username, Trainer updatedTrainer) {

        MDC.put("operation", "updateTrainer");
        MDC.put("username", username);

        log.debug("Starting update for trainer: {}", username);

        TrainerEntity trainerEntity = trainerRepo.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Trainer not found for update: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });

        // Check if name changed
        boolean nameChanged = !trainerEntity.getUserEntity().getFirstName().equals(updatedTrainer.getFirstName()) ||
                !trainerEntity.getUserEntity().getLastName().equals(updatedTrainer.getLastName());

        // Update the entity fields
        trainerMapper.updateEntity(updatedTrainer, trainerEntity);

        // Regenerate username if name changed
        if (nameChanged) {
            String baseUsername = updatedTrainer.getFirstName() + "." + updatedTrainer.getLastName();
            String newUsername = generateUniqueUsername(baseUsername);
            trainerEntity.getUserEntity().setUsername(newUsername);
            log.debug("Username updated from {} to {}", username, newUsername);
        }

        TrainerEntity saved = trainerRepo.save(trainerEntity);
        String finalUsername = saved.getUserEntity().getUsername();

        log.info("Trainer updated successfully: {} -> {}", username, finalUsername);
        return trainerMapper.toTrainerModel(saved);
    }


    @Override
    public List<Trainer> getAllTrainers() {
        log.debug("Fetching all trainers");

        List<TrainerEntity> trainerEntities = trainerRepo.findAll();
        List<Trainer> trainers = trainerMapper.toTrainerModels(trainerEntities);

        log.info("Total trainers fetched: {}", trainers.size());
        return trainers;
    }
}