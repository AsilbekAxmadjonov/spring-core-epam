package org.example.services.impl.dbImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.model.Training;
import org.example.repository.TraineeRepo;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingRepo;
import org.example.services.TrainingService;
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

    @Override
    @Transactional(readOnly = true)
    public List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType
    ) {
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
    public Training addTraining(@Valid Training training) {
        log.debug("Starting training creation: trainee={}, trainer={}, name={}",
                training.getTraineeUsername(),
                training.getTrainerUsername(),
                training.getTrainingName()
        );

        TraineeEntity traineeEntity = traineeRepo.findByUsername(training.getTraineeUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainee not found: " + training.getTraineeUsername()));

        TrainerEntity trainerEntity = trainerRepo.findByUsername(training.getTrainerUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainer not found: " + training.getTrainerUsername()));

        TrainingEntity trainingEntity = trainingMapper.toTrainingEntity(training);

        trainingEntity.setTraineeEntity(traineeEntity);
        trainingEntity.setTrainerEntity(trainerEntity);

        TrainingEntity savedTrainingEntity = trainingRepo.save(trainingEntity);

        log.info("Training created successfully with ID {}", savedTrainingEntity.getId());

        return trainingMapper.toTrainingModel(savedTrainingEntity);
    }

    @Override
    public void createTraining(@Valid Training training) {
        log.debug("Delegating createTraining to addTraining()");
        addTraining(training);
    }

    @Override
    @Transactional(readOnly = true)
    public Training getTraining(String name) {
        log.debug("Fetching training by name: {}", name);

        TrainingEntity trainingEntity = trainingRepo.findByTrainingName(name)
                .orElseThrow(() -> new UserNotFoundException("Training not found: " + name));

        log.info("Training found: {}", name);

        return trainingMapper.toTrainingModel(trainingEntity);
    }

    @Override
    public List<Training> listAll() {
        log.debug("Fetching all trainings");

        List<TrainingEntity> trainingEntities = trainingRepo.findAll();

        log.info("Fetched {} total trainings", trainingEntities.size());

        return trainingMapper.toTrainingModels(trainingEntities);
    }



}
