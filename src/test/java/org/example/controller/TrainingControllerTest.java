package org.example.controller;

import org.example.dto.response.TrainingResponse;
import org.example.entity.TrainingTypeEntity;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepo;
import org.example.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() {
        TrainingTypeEntity typeEntity = new TrainingTypeEntity();
        typeEntity.setTrainingTypeName("Yoga");
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(typeEntity));

        Training training = Training.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .trainingType(new TrainingType())
                .build();

        when(trainingService.addTraining(any(Training.class))).thenReturn(training);

        ResponseEntity<?> response = trainingController.createTraining(
                org.example.dto.request.TrainingRequest.builder()
                        .traineeUsername("trainee1")
                        .trainerUsername("trainer1")
                        .trainingName("Morning Yoga")
                        .trainingType("Yoga")
                        .trainingDate(LocalDate.now())
                        .trainingDurationMinutes(60)
                        .build()
        );

        assertEquals(201, response.getStatusCodeValue());
        verify(trainingService, times(1)).addTraining(any(Training.class));
    }

    @Test
    void testGetAllTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        Training t2 = Training.builder().trainingName("T2").build();
        when(trainingService.listAll()).thenReturn(List.of(t1, t2));

        ResponseEntity<?> response = trainingController.getAllTrainings();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(trainingService, times(1)).listAll();
    }

    @Test
    void testGetTraineeTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        when(trainingService.getTraineeTrainings("trainee1", null, null, null, null))
                .thenReturn(List.of(t1));

        ResponseEntity<?> response = trainingController.getTraineeTrainings("trainee1", null, null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        verify(trainingService, times(1)).getTraineeTrainings("trainee1", null, null, null, null);
    }

    @Test
    void testGetTrainerTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        when(trainingService.getTrainerTrainings("trainer1", null, null, null))
                .thenReturn(List.of(t1));

        ResponseEntity<?> response = trainingController.getTrainerTrainings("trainer1", null, null, null);

        assertEquals(200, response.getStatusCodeValue());
        verify(trainingService, times(1)).getTrainerTrainings("trainer1", null, null, null);
    }

    @Test
    void testGetTrainingByName() {
        Training training = Training.builder().trainingName("Yoga").build();
        when(trainingService.getTraining("Yoga")).thenReturn(training);

        ResponseEntity<TrainingResponse> response = trainingController.getTrainingByName("Yoga");

        assertEquals(200, response.getStatusCodeValue());
        TrainingResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Yoga", body.getTrainingName());
        verify(trainingService, times(1)).getTraining("Yoga");
    }

}
