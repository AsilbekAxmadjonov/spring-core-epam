package org.example.config;

import org.example.model.Trainee;
import org.example.model.Trainer;
import org.example.model.Training;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StorageConfigTest {

    @Autowired
    @Qualifier(StorageConfig.TRAINER_STORAGE_BEAN)
    private Map<String, Trainer> trainerStorage;

    @Autowired
    @Qualifier(StorageConfig.TRAINEE_STORAGE_BEAN)
    private Map<String, Trainee> traineeStorage;

    @Autowired
    @Qualifier(StorageConfig.TRAINING_STORAGE_BEAN)
    private Map<String, Training> trainingStorage;

    @Test
    void testTrainerStorageBeanExists() {
        assertNotNull(trainerStorage);
        assertTrue(trainerStorage.isEmpty());
    }

    @Test
    void testTraineeStorageBeanExists() {
        assertNotNull(traineeStorage);
        assertTrue(traineeStorage.isEmpty());
    }

    @Test
    void testTrainingStorageBeanExists() {
        assertNotNull(trainingStorage);
        assertTrue(trainingStorage.isEmpty());
    }
}
