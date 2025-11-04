package org.example.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer {

    @Value("${data.trainers.path}")
    private String trainersFilePath;

    @Value("${data.trainees.path}")
    private String traineesFilePath;

    @Value("${data.trainings.path}")
    private String trainingsFilePath;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final Map<String, Trainer> trainerStorage;
    private final Map<String, Trainee> traineeStorage;
    private final Map<String, Training> trainingStorage;

    public StorageInitializer(Map<String, Trainer> trainerStorage,
                              Map<String, Trainee> traineeStorage,
                              Map<String, Training> trainingStorage) {
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
            e.printStackTrace();
        }
    }

    private void loadTrainers() throws Exception {
        List<Trainer> trainers = objectMapper.readValue(
                new ClassPathResource(trainersFilePath).getInputStream(),
                new TypeReference<List<Trainer>>() {}
        );
        trainers.forEach(t -> trainerStorage.put(t.getUsername(), t));
        System.out.println("Loaded " + trainers.size() + " trainers from JSON.");
    }

    private void loadTrainees() throws Exception {
        List<Trainee> trainees = objectMapper.readValue(
                new ClassPathResource(traineesFilePath).getInputStream(),
                new TypeReference<List<Trainee>>() {}
        );
        trainees.forEach(t -> traineeStorage.put(t.getUsername(), t));
        System.out.println("Loaded " + trainees.size() + " trainees from JSON.");
    }

    private void loadTrainings() throws Exception {
        List<Training> trainings = objectMapper.readValue(
                new ClassPathResource(trainingsFilePath).getInputStream(),
                new TypeReference<List<Training>>() {}
        );
        trainings.forEach(t -> trainingStorage.put(t.getTrainingName(), t));
        System.out.println("Loaded " + trainings.size() + " trainings from JSON.");
    }
}
