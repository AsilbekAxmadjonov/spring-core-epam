package org.example.init;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {

    @Value("${data.trainers.path}")
    private String trainersFilePath;

    @Value("${data.trainees.path}")
    private String traineesFilePath;

    @Value("${data.trainings.path}")
    private String trainingsFilePath;

    private final ResourceLoader resourceLoader;

    public StorageInitializer(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        try {
            if (beanName.equals("trainerStorage")) {
                loadTrainers((Map<String, Trainer>) bean, trainersFilePath);
            } else if (beanName.equals("traineeStorage")) {
                loadTrainees((Map<String, Trainee>) bean, traineesFilePath);
            } else if (beanName.equals("trainingStorage")) {
                loadTrainings((Map<String, Training>) bean, trainingsFilePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

    private void loadTrainers(Map<String, Trainer> storage, String path) {
        loadFile(storage, path, parts -> {
            if (parts.length >= 4) {
                Trainer trainer = new Trainer(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        parts[3].trim()
                );
                storage.put(trainer.getUsername(), trainer);
            }
        });
    }

    private void loadTrainees(Map<String, Trainee> storage, String path) {
        loadFile(storage, path, parts -> {
            if (parts.length >= 5) {
                Trainee trainee = new Trainee(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        LocalDate.parse(parts[3].trim()),
                        parts[4].trim()
                );
                storage.put(trainee.getUsername(), trainee);
            }
        });
    }

    private void loadTrainings(Map<String, Training> storage, String path) {
        loadFile(storage, path, parts -> {
            if (parts.length >= 6) {
                Training training = new Training(
                        parts[0].trim(),
                        parts[1].trim(),
                        parts[2].trim(),
                        new TrainingType(parts[3].trim()),
                        LocalDate.parse(parts[4].trim()),
                        Integer.parseInt(parts[5].trim())
                );
                storage.put(training.getTrainingName(), training);
            }
        });
    }

    private void loadFile(Map<String, ?> storage, String path, LineParser parser) {
        Resource resource = resourceLoader.getResource(path);
        if (!resource.exists()) {
            System.err.println("Resource not found: " + path);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                parser.parse(line.split(","));
            }
            System.out.println("Loaded data from: " + path);
        } catch (IOException e) {
            System.err.println("Failed to read resource: " + path);
        }
    }

    @FunctionalInterface
    private interface LineParser {
        void parse(String[] parts);
    }
}
