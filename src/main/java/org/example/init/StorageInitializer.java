package org.example.init;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    Trainer trainer = new Trainer(
                            parts[0].trim(), // username
                            parts[1].trim(), // firstName
                            parts[2].trim(), // lastName
                            parts[3].trim()  // specialization
                    );
                    storage.put(trainer.getUsername(), trainer);
                }
            }
            System.out.println("Loaded trainers from: " + path);
        } catch (IOException e) {
            System.err.println("File not found: " + path);
        }
    }

    private void loadTrainees(Map<String, Trainee> storage, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
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
            }
            System.out.println("Loaded trainees from: " + path);
        } catch (IOException e) {
            System.err.println("File not found: " + path);
        }
    }

    private void loadTrainings(Map<String, Training> storage, String path) {
        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    Training training = new Training(
                            parts[0].trim(), // traineeId
                            parts[1].trim(), // trainerId
                            parts[2].trim(), // trainingName
                            new TrainingType(parts[3].trim()), // or TrainingType.valueOf(...)
                            LocalDate.parse(parts[4].trim()), // date
                            Integer.parseInt(parts[5].trim()) // duration
                    );
                    storage.put(training.getTrainingName(), training);
                }
            }
            System.out.println("Loaded trainings from: " + path);
        } catch (IOException e) {
            System.err.println("File not found: " + path);
        }
    }
}
