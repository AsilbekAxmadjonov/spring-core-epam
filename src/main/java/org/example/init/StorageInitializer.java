package org.example.init;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Component
public class StorageInitializer implements BeanPostProcessor {

    @Value("${data.trainers.path}")
    private String trainersFilePath;

    @Value("${data.trainees.path}")
    private String traineesFilePath;

    @Value("${data.trainings.path}")
    private String trainingsFilePath;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

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

    private void loadTrainers(Map<String, Trainer> storage, String path) throws Exception {
        List<Trainer> trainers = objectMapper.readValue(
                new ClassPathResource(path).getInputStream(),
                new TypeReference<List<Trainer>>() {}
        );
        trainers.forEach(t -> storage.put(t.getUsername(), t));
        System.out.println("Loaded " + trainers.size() + " trainers from JSON.");
    }

    private void loadTrainees(Map<String, Trainee> storage, String path) throws Exception {
        List<Trainee> trainees = objectMapper.readValue(
                new ClassPathResource(path).getInputStream(),
                new TypeReference<List<Trainee>>() {}
        );
        trainees.forEach(t -> storage.put(t.getUsername(), t));
        System.out.println("Loaded " + trainees.size() + " trainees from JSON.");
    }

    private void loadTrainings(Map<String, Training> storage, String path) throws Exception {
        List<Training> trainings = objectMapper.readValue(
                new ClassPathResource(path).getInputStream(),
                new TypeReference<List<Training>>() {}
        );
        trainings.forEach(t -> storage.put(t.getTrainingName(), t));
        System.out.println("Loaded " + trainings.size() + " trainings from JSON.");
    }
}
