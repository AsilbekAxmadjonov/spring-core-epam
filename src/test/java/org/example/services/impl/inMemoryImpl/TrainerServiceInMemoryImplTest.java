package org.example.services.impl.inMemoryImpl;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.example.security.AuthenticationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceInMemoryImplTest {

    private TrainerDao trainerDao;
    private TrainerServiceInMemoryImpl trainerService;
    private MockedStatic<AuthenticationContext> authContextMock;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class);
        trainerService = new TrainerServiceInMemoryImpl();
        trainerService.setTrainerDao(trainerDao);

        authContextMock = mockStatic(AuthenticationContext.class);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
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

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("alice01");
        when(trainerDao.findByUsername("alice01")).thenReturn(trainer);

        Optional<Trainer> result = trainerService.getTrainerByUsername("alice01");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(trainerDao, times(1)).findByUsername("alice01");
    }

    @Test
    void testGetTrainerByUsername_NotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(trainerDao.findByUsername("unknown")).thenReturn(null);

        Optional<Trainer> result = trainerService.getTrainerByUsername("unknown");

        assertTrue(result.isEmpty());
        verify(trainerDao, times(1)).findByUsername("unknown");
    }

    @Test
    void testGetTrainerByUsername_NotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.getTrainerByUsername("alice01"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerDao, never()).findByUsername(anyString());
    }

    @Test
    void testGetTrainerByUsername_DifferentUser() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.getTrainerByUsername("alice01"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerDao, never()).findByUsername(anyString());
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer("bob01", "Bob", "Smith");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("bob01");

        Trainer result = trainerService.updateTrainer("bob01", trainer);

        verify(trainerDao, times(1)).update(trainer);
        assertEquals(trainer, result);
    }

    @Test
    void testUpdateTrainer_NotAuthenticated() {
        Trainer trainer = createTrainer("bob01", "Bob", "Smith");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.updateTrainer("bob01", trainer));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerDao, never()).update(any());
    }

    @Test
    void testUpdateTrainer_DifferentUser() {
        Trainer trainer = createTrainer("bob01", "Bob", "Smith");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.updateTrainer("bob01", trainer));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerDao, never()).update(any());
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