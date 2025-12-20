package org.example.services;

import jakarta.validation.Valid;
import org.example.api.dto.request.TrainingRequest;
import org.example.persistance.model.Training;

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

    Training createTraining(@Valid TrainingRequest request);

    Training getTraining(String name);

    List<Training> listAll();

}
