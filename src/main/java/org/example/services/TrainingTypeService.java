package org.example.services;

import org.example.model.TrainingType;
import java.util.List;
import java.util.Optional;

public interface TrainingTypeService {
    Optional<TrainingType> getTrainingTypeByName(String name);
    List<TrainingType> getAllTrainingTypes();
}
