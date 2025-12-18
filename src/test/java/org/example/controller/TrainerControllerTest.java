package org.example.controller;

import org.example.dto.request.TrainerRequest;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainer;
import org.example.services.TrainerService;
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

class TrainerControllerTest {

    @Mock
    private TrainerService trainerService;

    @InjectMocks
    private TrainerController trainerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTrainer() {
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setSpecialization("Yoga");

        Trainer created = Trainer.builder()
                .username("alice123")
                .password("pass123".toCharArray())
                .token("token123")
                .build();

        when(trainerService.createTrainer(any(Trainer.class))).thenReturn(created);

        ResponseEntity<?> response = trainerController.createTrainer(request);

        assertEquals(201, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        verify(trainerService, times(1)).createTrainer(any(Trainer.class));
    }

    @Test
    void testGetTrainerByUsernameFound() {
        String username = "alice123";
        Trainer trainer = Trainer.builder()
                .username(username)
                .firstName("Alice")
                .lastName("Smith")
                .specialization("Yoga")
                .build();

        when(trainerService.getTrainerByUsername(username)).thenReturn(Optional.of(trainer));

        ResponseEntity<Trainer> response = trainerController.getTrainerByUsername(username);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, response.getBody().getUsername());
        verify(trainerService, times(1)).getTrainerByUsername(username);
    }

    @Test
    void testGetTrainerByUsernameNotFound() {
        String username = "unknown";
        when(trainerService.getTrainerByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> trainerController.getTrainerByUsername(username));
        verify(trainerService, times(1)).getTrainerByUsername(username);
    }

    @Test
    void testGetAllTrainers() {
        List<Trainer> trainers = List.of(
                Trainer.builder().username("alice123").build(),
                Trainer.builder().username("bob456").build()
        );

        when(trainerService.getAllTrainers()).thenReturn(trainers);

        ResponseEntity<List<Trainer>> response = trainerController.getAllTrainers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
        verify(trainerService, times(1)).getAllTrainers();
    }

    @Test
    void testUpdateTrainer() {
        String username = "alice123";
        TrainerRequest request = new TrainerRequest();
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setSpecialization("Pilates");
        request.setIsActive(true);

        Trainer updated = Trainer.builder()
                .username(username)
                .firstName("Alice")
                .lastName("Smith")
                .specialization("Pilates")
                .isActive(true)
                .build();

        when(trainerService.updateTrainer(eq(username), any(Trainer.class))).thenReturn(updated);

        ResponseEntity<Trainer> response = trainerController.updateTrainer(username, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(username, response.getBody().getUsername());
        verify(trainerService, times(1)).updateTrainer(eq(username), any(Trainer.class));
    }
}
