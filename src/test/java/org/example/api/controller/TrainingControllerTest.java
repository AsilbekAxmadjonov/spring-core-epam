package org.example.api.controller;

import org.example.api.controller.TrainingController;
import org.example.api.dto.request.TrainingRequest;
import org.example.api.dto.response.TrainingResponse;
import org.example.mapper.TrainingResponseMapper;
import org.example.persistance.model.Training;
import org.example.persistance.model.TrainingType;
import org.example.services.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingControllerTest {

    @Mock
    private TrainingService trainingService;

    @Mock
    private TrainingResponseMapper trainingResponseMapper;

    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTraining() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        Training training = Training.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingType(new TrainingType("Yoga"))
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        TrainingResponse responseDto = TrainingResponse.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Morning Yoga")
                .trainingType("Yoga")
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        when(trainingService.createTraining(request)).thenReturn(training);
        when(trainingResponseMapper.toResponse(training)).thenReturn(responseDto);

        ResponseEntity<TrainingResponse> response = trainingController.createTraining(request);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals(responseDto, response.getBody());

        verify(trainingService, times(1)).createTraining(request);
        verify(trainingResponseMapper, times(1)).toResponse(training);
    }

    @Test
    void testGetAllTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        Training t2 = Training.builder().trainingName("T2").build();
        when(trainingService.listAll()).thenReturn(List.of(t1, t2));

        when(trainingResponseMapper.toResponse(t1)).thenReturn(
                TrainingResponse.builder().trainingName("T1").build()
        );
        when(trainingResponseMapper.toResponse(t2)).thenReturn(
                TrainingResponse.builder().trainingName("T2").build()
        );

        ResponseEntity<List<TrainingResponse>> response = trainingController.getAllTrainings();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(trainingService, times(1)).listAll();
    }

    @Test
    void testGetTraineeTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        when(trainingService.getTraineeTrainings("trainee1", null, null, null, null))
                .thenReturn(List.of(t1));
        when(trainingResponseMapper.toResponse(t1)).thenReturn(
                TrainingResponse.builder().trainingName("T1").build()
        );

        ResponseEntity<List<TrainingResponse>> response = trainingController.getTraineeTrainings(
                "trainee1", null, null, null, null
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(trainingService, times(1)).getTraineeTrainings("trainee1", null, null, null, null);
    }

    @Test
    void testGetTrainerTrainings() {
        Training t1 = Training.builder().trainingName("T1").build();
        when(trainingService.getTrainerTrainings("trainer1", null, null, null))
                .thenReturn(List.of(t1));
        when(trainingResponseMapper.toResponse(t1)).thenReturn(
                TrainingResponse.builder().trainingName("T1").build()
        );

        ResponseEntity<List<TrainingResponse>> response = trainingController.getTrainerTrainings(
                "trainer1", null, null, null
        );

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(trainingService, times(1)).getTrainerTrainings("trainer1", null, null, null);
    }

    @Test
    void testGetTrainingByName() {
        Training training = Training.builder()
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingName("Yoga")
                .trainingType(new TrainingType("Yoga"))
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        when(trainingService.getTraining("Yoga")).thenReturn(training);

        ResponseEntity<TrainingResponse> response = trainingController.getTrainingByName("Yoga");

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("Yoga", response.getBody().getTrainingName());
        assertEquals("trainee1", response.getBody().getTraineeUsername());
        assertEquals("trainer1", response.getBody().getTrainerUsername());
        assertEquals("Yoga", response.getBody().getTrainingType());
        assertEquals(60, response.getBody().getTrainingDurationMinutes());

        verify(trainingService, times(1)).getTraining("Yoga");

    }

}
