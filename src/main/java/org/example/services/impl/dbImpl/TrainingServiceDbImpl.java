package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.request.TrainingRequest;
import org.example.persistance.entity.TraineeEntity;
import org.example.persistance.entity.TrainerEntity;
import org.example.persistance.entity.TrainingEntity;
import org.example.persistance.entity.TrainingTypeEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.persistance.model.Training;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.example.services.TrainingService;
import org.slf4j.MDC;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@Primary
@Validated
@RequiredArgsConstructor
@Transactional
public class TrainingServiceDbImpl implements TrainingService {

    private final TrainingRepo trainingRepo;
    private final TraineeRepo traineeRepo;
    private final TrainerRepo trainerRepo;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeRepo trainingTypeRepo;

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType
    ) {
//        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticatedUser == null || !authenticatedUser.equals(traineeUsername)) {
//            log.error("Authentication failed for getTraineeTrainings: {} (authenticated: {})", traineeUsername, authenticatedUser);
//            throw new SecurityException("User not authenticated");
//        }

        log.debug("Fetching trainee trainings: username={}, from={}, to={}, trainerName={}, trainingType={}",
                traineeUsername, fromDate, toDate, trainerName, trainingType);

        List<TrainingEntity> trainingEntities = trainingRepo.findTraineeTrainings(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );

        log.info("Fetched {} trainee trainings for username={}", trainingEntities.size(), traineeUsername);

        return trainingMapper.toTrainingModels(trainingEntities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTrainerTrainings(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    ) {
//        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticatedUser == null || !authenticatedUser.equals(trainerUsername)) {
//            log.error("Authentication failed for getTrainerTrainings: {} (authenticated: {})", trainerUsername, authenticatedUser);
//            throw new SecurityException("User not authenticated");
//        }

        log.debug("Fetching trainer trainings: username={}, from={}, to={}, traineeName={}",
                trainerUsername, fromDate, toDate, traineeName);

        List<TrainingEntity> trainingEntities = trainingRepo.findTrainerTrainings(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );

        log.info("Fetched {} trainer trainings for username={}", trainingEntities.size(), trainerUsername);

        return trainingMapper.toTrainingModels(trainingEntities);
    }


    @Override
    public Training createTraining(@Valid TrainingRequest request) {

        TraineeEntity traineeEntity = traineeRepo.findByUsername(request.getTraineeUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainee not found: " + request.getTraineeUsername()));

        TrainerEntity trainerEntity = trainerRepo.findByUsername(request.getTrainerUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainer not found: " + request.getTrainerUsername()));

        TrainingTypeEntity trainingTypeEntity = trainingTypeRepo
                .findByTrainingTypeName(request.getTrainingType())
                .orElseThrow(() ->
                        new IllegalArgumentException("Invalid training type: " + request.getTrainingType()));

        TrainingEntity entity = new TrainingEntity();
        entity.setTrainingName(request.getTrainingName());
        entity.setTrainingDate(request.getTrainingDate());
        entity.setTrainingDurationMinutes(request.getTrainingDurationMinutes());
        entity.setTraineeEntity(traineeEntity);
        entity.setTrainerEntity(trainerEntity);
        entity.setTrainingTypeEntity(trainingTypeEntity);

        TrainingEntity saved = trainingRepo.save(entity);

        return trainingMapper.toTrainingModel(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public Training getTraining(String name) {
//        String authenticatedUser = AuthenticationContext.getAuthenticatedUser();
//
//        if (authenticatedUser == null) {
//            log.error("Authentication failed for getTraining");
//            throw new SecurityException("User not authenticated");
//        }

        log.debug("Fetching training by name: {}", name);

        TrainingEntity trainingEntity = trainingRepo.findByTrainingName(name)
                .orElseThrow(() -> new UserNotFoundException("Training not found: " + name));

        log.info("Training found: {}", name);

        return trainingMapper.toTrainingModel(trainingEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Training> listAll() {
        log.debug("Fetching all trainings");

        List<TrainingEntity> trainingEntities = trainingRepo.findAll();

        log.info("Fetched {} total trainings", trainingEntities.size());

        return trainingMapper.toTrainingModels(trainingEntities);
    }
}