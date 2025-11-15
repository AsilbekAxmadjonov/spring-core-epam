package org.example.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StorageInitializer {

    @Value("${data.trainers.path}")
    private String trainersFilePath;

    @Value("${data.trainees.path}")
    private String traineesFilePath;

    @Value("${data.trainings.path}")
    private String trainingsFilePath;

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
                new ClassPathResource(trainersFilePath).getInputStream(),
                new TypeReference<List<Trainer>>() {}
        );
        trainers.forEach(t -> trainerStorage.put(t.getUsername(), t));
        log.info("Loaded {} trainers", trainers.size());
    }

    private void loadTrainees() throws Exception {
        List<Trainee> trainees = objectMapper.readValue(
                new ClassPathResource(traineesFilePath).getInputStream(),
                new TypeReference<List<Trainee>>() {}
        );
        trainees.forEach(t -> traineeStorage.put(t.getUsername(), t));
        log.info("Loaded {} trainees", trainees.size());
    }

    private void loadTrainings() throws Exception {
        List<Training> trainings = objectMapper.readValue(
                new ClassPathResource(trainingsFilePath).getInputStream(),
                new TypeReference<List<Training>>() {}
        );
        trainings.forEach(t -> trainingStorage.put(t.getTrainingName(), t));
        log.info("Loaded {} trainings", trainings.size());
    }
}
