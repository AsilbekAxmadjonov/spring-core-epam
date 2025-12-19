package org.example.services;

import jakarta.validation.Valid;
import org.example.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {

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

    Training addTraining(@Valid Training training);

    void createTraining(@Valid Training training);

    Training getTraining(String name);

    List<Training> listAll();

}
