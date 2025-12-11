package org.example.services.impl.inMemoryImpl;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceInMemoryImplTest {

    private TrainerDao trainerDao;
    private AuthenticationService authenticationService;
    private TrainerServiceInMemoryImpl trainerService;
    private final char[] dummyPassword = "dummyPass".toCharArray();

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        authenticationService = mock(AuthenticationService.class);
        trainerService = new TrainerServiceInMemoryImpl();
        trainerService.setTrainerDao(trainerDao);
        trainerService.setAuthenticationService(authenticationService);
    }

    private Trainer createTrainer(String username, String first, String last) {
        return Trainer.builder()
                .username(username)
                .firstName(first)
                .lastName(last)
                .password("123".toCharArray())
                .isActive(true)
                .build();
    }

    @Test
    void testCreateTrainer() {
        Trainer trainer = createTrainer("john01", "John", "Doe");

        Trainer result = trainerService.createTrainer(trainer);

        verify(trainerDao, times(1)).save(trainer);
        assertEquals(trainer, result);
    }

    @Test
    void testGetTrainerByUsername_Found() {
        Trainer trainer = createTrainer("alice01", "Alice", "Brown");
        when(trainerDao.findByUsername("alice01")).thenReturn(trainer);
        when(authenticationService.authenticate("alice01", dummyPassword)).thenReturn(trainer);

        Optional<Trainer> result = trainerService.getTrainerByUsername("alice01", dummyPassword);

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(authenticationService, times(1)).authenticate("alice01", dummyPassword);
        verify(trainerDao, times(1)).findByUsername("alice01");
    }

    @Test
    void testGetTrainerByUsername_NotFound() {
        Trainer mockUser = createTrainer("unknown", "Unknown", "User");
        when(trainerDao.findByUsername("unknown")).thenReturn(null);
        when(authenticationService.authenticate("unknown", dummyPassword)).thenReturn(mockUser);

        Optional<Trainer> result = trainerService.getTrainerByUsername("unknown", dummyPassword);

        assertTrue(result.isEmpty());
        verify(authenticationService, times(1)).authenticate("unknown", dummyPassword);
        verify(trainerDao, times(1)).findByUsername("unknown");
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer("bob01", "Bob", "Smith");
        when(authenticationService.authenticate("bob01", dummyPassword)).thenReturn(trainer);

        Trainer result = trainerService.updateTrainer("bob01", dummyPassword, trainer);

        verify(authenticationService, times(1)).authenticate("bob01", dummyPassword);
        verify(trainerDao, times(1)).update(trainer);
        assertEquals(trainer, result);
    }

    @Test
    void testGetAllTrainers() {
        Trainer t1 = createTrainer("t1", "Tom", "Jones");
        Trainer t2 = createTrainer("t2", "Jane", "Miller");
        when(trainerDao.findAll()).thenReturn(List.of(t1, t2));

        List<Trainer> trainers = trainerService.getAllTrainers();

        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(t1));
        assertTrue(trainers.contains(t2));
        verify(trainerDao, times(1)).findAll();
    }
}