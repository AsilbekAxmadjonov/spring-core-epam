package org.example.services.impl;

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

import java.util.List;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
@Transactional
public class TrainingServiceDbImpl implements TrainingService {

    private final TrainingRepo trainingRepo;
    private final TraineeRepo traineeRepo;
    private final TrainerRepo trainerRepo;
    private final TrainingMapper trainingMapper;

    @Override
    public List<Training> getTraineeTrainings(
            String traineeUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String trainerName,
            String trainingType
    ) {
        log.info("Fetching trainee trainings for username: {}", traineeUsername);

        List<TrainingEntity> list = trainingRepo.findTraineeTrainings(
                traineeUsername,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );

        return trainingMapper.toTrainingModels(list);
    }

    @Override
    public List<Training> getTrainerTrainings(
            String trainerUsername,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            String traineeName
    ) {
        log.info("Fetching trainer trainings for username: {}", trainerUsername);

        List<TrainingEntity> list = trainingRepo.findTrainerTrainings(
                trainerUsername,
                fromDate,
                toDate,
                traineeName
        );

        return trainingMapper.toTrainingModels(list);
    }

    @Override
    public Training addTraining(Training training) {
        log.info("Adding new training for trainee={}, trainer={}, name={}",
                training.getTraineeUsername(),
                training.getTrainerUsername(),
                training.getTrainingName()
        );

        TraineeEntity trainee = traineeRepo.findByUsername(training.getTraineeUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainee not found: " + training.getTraineeUsername()));

        TrainerEntity trainer = trainerRepo.findByUsername(training.getTrainerUsername())
                .orElseThrow(() ->
                        new UserNotFoundException("Trainer not found: " + training.getTrainerUsername()));

        TrainingEntity entity = trainingMapper.toTrainingEntity(training);

        entity.setTraineeEntity(trainee);
        entity.setTrainerEntity(trainer);

        TrainingEntity saved = trainingRepo.save(entity);

        log.info("Training saved with ID {}", saved.getId());

        return trainingMapper.toTrainingModel(saved);
    }

    @Override
    public void createTraining(Training training) {
        addTraining(training); // reuse addTraining
    }

    @Override
    public Training getTraining(String name) {
        TrainingEntity entity = trainingRepo.findByTrainingName(name)
                .orElseThrow(() -> new UserNotFoundException("Training not found: " + name));
        return trainingMapper.toTrainingModel(entity);
    }

    @Override
    public List<Training> listAll() {
        List<TrainingEntity> entities = trainingRepo.findAll();
        return trainingMapper.toTrainingModels(entities);
    }


}
