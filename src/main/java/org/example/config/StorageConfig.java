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

    @Bean("trainerStorage")
    public Map<String, Trainer> trainerStorage() {
        return new HashMap<>();
    }

    @Bean("traineeStorage")
    public Map<String, Trainee> traineeStorage() {
        return new HashMap<>();
    }

    @Bean("trainingStorage")
    public Map<String, Training> trainingStorage() {
        return new HashMap<>();
    }
}
