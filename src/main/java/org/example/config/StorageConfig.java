package org.example.config;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class StorageConfig {

    public static final String TRAINER_STORAGE_BEAN = "trainerStorage";
    public static final String TRAINEE_STORAGE_BEAN = "traineeStorage";
    public static final String TRAINING_STORAGE_BEAN = "trainingStorage";

    @Bean(name = TRAINER_STORAGE_BEAN)
    public Map<String, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean(name = TRAINEE_STORAGE_BEAN)
    public Map<String, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean(name = TRAINING_STORAGE_BEAN)
    public Map<String, Training> trainingStorage() {
        return new HashMap<>();
    }
}

