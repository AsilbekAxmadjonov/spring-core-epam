package org.example.init;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.persistance.model.Trainee;
import org.example.persistance.model.Trainer;
import org.example.persistance.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class StorageInitializerTest {

    private ObjectMapper objectMapper;
    private Map<String, Trainer> trainerStorage;
    private Map<String, Trainee> traineeStorage;
    private Map<String, Training> trainingStorage;

    private StorageInitializer initializer;

    private Resource trainersFile;
    private Resource traineesFile;
    private Resource trainingsFile;

    @BeforeEach
    void setup() throws Exception {
        objectMapper = new ObjectMapper();
        trainerStorage = new HashMap<>();
        traineeStorage = new HashMap<>();
        trainingStorage = new HashMap<>();

        trainersFile = Mockito.mock(Resource.class);
        traineesFile = Mockito.mock(Resource.class);
        trainingsFile = Mockito.mock(Resource.class);

        // Sample JSON data
        String trainerJson = "[{\"username\":\"trainer1\"}]";
        String traineeJson = "[{\"username\":\"trainee1\"}]";
        String trainingJson = "[{\"trainingName\":\"training1\"}]";

        when(trainersFile.getInputStream()).thenReturn(
                new ByteArrayInputStream(trainerJson.getBytes(StandardCharsets.UTF_8))
        );
        when(traineesFile.getInputStream()).thenReturn(
                new ByteArrayInputStream(traineeJson.getBytes(StandardCharsets.UTF_8))
        );
        when(trainingsFile.getInputStream()).thenReturn(
                new ByteArrayInputStream(trainingJson.getBytes(StandardCharsets.UTF_8))
        );

        initializer = new StorageInitializer(objectMapper, trainerStorage, traineeStorage, trainingStorage);

        // Inject private Resource fields via reflection
        setPrivateField(initializer, "trainersFile", trainersFile);
        setPrivateField(initializer, "traineesFile", traineesFile);
        setPrivateField(initializer, "trainingsFile", trainingsFile);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void testInitLoadsAllData() throws Exception {
        initializer.init();

        assertEquals(1, trainerStorage.size());
        assertEquals(1, traineeStorage.size());
        assertEquals(1, trainingStorage.size());

        assertEquals("trainer1", trainerStorage.get("trainer1").getUsername());
        assertEquals("trainee1", traineeStorage.get("trainee1").getUsername());
        assertEquals("training1", trainingStorage.get("training1").getTrainingName());
    }
}
