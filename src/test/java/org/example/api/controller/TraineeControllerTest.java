package org.example.api.controller;

import org.example.api.controller.TraineeController;
import org.example.api.dto.request.TraineeRequest;
import org.example.exception.UserNotFoundException;
import org.example.persistance.model.Trainee;
import org.example.services.TraineeService;
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

class TraineeControllerTest {

    @Mock
    private TraineeService traineeService;

    @InjectMocks
    private TraineeController traineeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainee() {
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");

        Trainee created = Trainee.builder()
                .username("john123")
                .password("pass123".toCharArray())
                .token("token123")
                .build();

        when(traineeService.createTrainee(any(Trainee.class))).thenReturn(created);

        ResponseEntity<?> response = traineeController.createTrainee(request);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(traineeService, times(1)).createTrainee(any(Trainee.class));
    }

    @Test
    void testGetTraineeByUsernameFound() {
        String username = "john123";
        Trainee trainee = Trainee.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(traineeService.getTraineeByUsername(username)).thenReturn(Optional.of(trainee));

        ResponseEntity<Trainee> response = traineeController.getTraineeByUsername(username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, response.getBody().getUsername());
        verify(traineeService, times(1)).getTraineeByUsername(username);
    }

    @Test
    void testGetTraineeByUsernameNotFound() {
        String username = "unknown";
        when(traineeService.getTraineeByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> traineeController.getTraineeByUsername(username));
        verify(traineeService, times(1)).getTraineeByUsername(username);
    }

    @Test
    void testGetAllTrainees() {
        List<Trainee> trainees = List.of(
                Trainee.builder().username("john123").build(),
                Trainee.builder().username("jane456").build()
        );

        when(traineeService.getAllTrainees()).thenReturn(trainees);

        ResponseEntity<List<Trainee>> response = traineeController.getAllTrainees();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(traineeService, times(1)).getAllTrainees();
    }

    @Test
    void testUpdateTrainee() {
        String username = "john123";
        TraineeRequest request = new TraineeRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setIsActive(true);
        request.setDateOfBirth(LocalDate.of(2000, 1, 1));

        Trainee updated = Trainee.builder()
                .username(username)
                .firstName("John")
                .lastName("Doe")
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        when(traineeService.updateTrainee(eq(username), any(Trainee.class))).thenReturn(updated);

        ResponseEntity<Trainee> response = traineeController.updateTrainee(username, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, response.getBody().getUsername());
        verify(traineeService, times(1)).updateTrainee(eq(username), any(Trainee.class));
    }

    @Test
    void testDeleteTrainee() {
        String username = "john123";

        doNothing().when(traineeService).deleteTraineeByUsername(username);

        ResponseEntity<Void> response = traineeController.deleteTrainee(username);

        assertEquals(204, response.getStatusCodeValue());
        verify(traineeService, times(1)).deleteTraineeByUsername(username);
    }
}
