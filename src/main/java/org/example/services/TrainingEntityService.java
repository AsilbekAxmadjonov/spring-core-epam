package org.example.services;

import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingEntityService {

    List<Training> getTraineeTrainings(
            String traineeUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String trainerName,
            String trainingType
    );

    List<Training> getTrainerTrainings(
            String trainerUsername,
            LocalDate fromDate,
            LocalDate toDate,
            String traineeName
    );

    Training addTraining(Training training);
}
