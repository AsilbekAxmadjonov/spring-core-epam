package org.example.controller;

import org.example.dto.response.TrainingTypeResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.TrainingType;
import org.example.services.TrainingTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingTypeControllerTest {

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private TrainingTypeController trainingTypeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTrainingTypes() {
        TrainingType t1 = new TrainingType();
        t1.setTrainingTypeName("Yoga");
        TrainingType t2 = new TrainingType();
        t2.setTrainingTypeName("Fitness");

        when(trainingTypeService.getAllTrainingTypes()).thenReturn(List.of(t1, t2));

        ResponseEntity<List<TrainingTypeResponse>> response = trainingTypeController.getAllTrainingTypes();

        assertEquals(200, response.getStatusCodeValue());
        List<TrainingTypeResponse> body = response.getBody();
        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals("Yoga", body.get(0).getTrainingTypeName());
        assertEquals("Fitness", body.get(1).getTrainingTypeName());

        verify(trainingTypeService, times(1)).getAllTrainingTypes();
    }

    @Test
    void testGetTrainingTypeByNameFound() {
        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName("Yoga");

        when(trainingTypeService.getTrainingTypeByName("Yoga")).thenReturn(Optional.of(trainingType));

        ResponseEntity<TrainingTypeResponse> response = trainingTypeController.getTrainingTypeByName("Yoga");

        assertEquals(200, response.getStatusCodeValue());
        TrainingTypeResponse body = response.getBody();
        assertNotNull(body);
        assertEquals("Yoga", body.getTrainingTypeName());

        verify(trainingTypeService, times(1)).getTrainingTypeByName("Yoga");
    }

    @Test
    void testGetTrainingTypeByNameNotFound() {
        when(trainingTypeService.getTrainingTypeByName("Pilates")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> trainingTypeController.getTrainingTypeByName("Pilates"));

        verify(trainingTypeService, times(1)).getTrainingTypeByName("Pilates");
    }
}
