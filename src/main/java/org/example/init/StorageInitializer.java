package org.example.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.persistance.model.Trainee;
import org.example.persistance.model.Trainer;
import org.example.persistance.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Slf4j
//@Component
public class StorageInitializer {

    @Value("${data.trainers.path}")
    private Resource trainersFile;

    @Value("${data.trainees.path}")
    private Resource traineesFile;

    @Value("${data.trainings.path}")
    private Resource trainingsFile;

    private final ObjectMapper objectMapper;
    private final Map<String, Trainer> trainerStorage;
    private final Map<String, Trainee> traineeStorage;
    private final Map<String, Training> trainingStorage;

    public StorageInitializer(ObjectMapper objectMapper,
                              Map<String, Trainer> trainerStorage,
                              Map<String, Trainee> traineeStorage,
                              Map<String, Training> trainingStorage) {
        this.objectMapper = objectMapper;
        this.trainerStorage = trainerStorage;
        this.traineeStorage = traineeStorage;
        this.trainingStorage = trainingStorage;
    }

    @PostConstruct
    public void init() {
        try {
            loadTrainers();
            loadTrainees();
            loadTrainings();
        } catch (Exception e) {
            log.error("Failed to initialize storage", e);
        }
    }

    private void loadTrainers() throws Exception {
        List<Trainer> trainers = objectMapper.readValue(
                trainersFile.getInputStream(),
                new TypeReference<List<Trainer>>() {}
        );
        trainers.forEach(t -> trainerStorage.put(t.getUsername(), t));
        log.info("Loaded {} trainers", trainers.size());
    }

    private void loadTrainees() throws Exception {
        List<Trainee> trainees = objectMapper.readValue(
                traineesFile.getInputStream(),
                new TypeReference<List<Trainee>>() {}
        );
        trainees.forEach(t -> traineeStorage.put(t.getUsername(), t));
        log.info("Loaded {} trainees", trainees.size());
    }

    private void loadTrainings() throws Exception {
        List<Training> trainings = objectMapper.readValue(
                trainingsFile.getInputStream(),
                new TypeReference<List<Training>>() {}
        );
        trainings.forEach(t -> trainingStorage.put(t.getTrainingName(), t));
        log.info("Loaded {} trainings", trainings.size());
    }
}
