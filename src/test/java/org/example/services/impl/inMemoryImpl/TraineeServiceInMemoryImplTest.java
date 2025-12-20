package org.example.services.impl.inMemoryImpl;

import org.example.dao.TraineeDao;
import org.example.persistance.model.Trainee;
import org.example.security.AuthenticationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceInMemoryImplTest {

    private TraineeDao traineeDao;
    private TraineeServiceInMemoryImpl traineeService;
    private MockedStatic<AuthenticationContext> authContextMock;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        traineeService = new TraineeServiceInMemoryImpl();
        traineeService.setTraineeDao(traineeDao);

        authContextMock = mockStatic(AuthenticationContext.class);
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        Trainee result = traineeService.createTrainee(trainee);

        verify(traineeDao).save(trainee);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testGetTraineeByUsername_Found() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john");
        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        Optional<Trainee> found = traineeService.getTraineeByUsername("john");

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
        verify(traineeDao).findByUsername("john");
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(traineeDao.findByUsername("unknown")).thenReturn(null);

        Optional<Trainee> found = traineeService.getTraineeByUsername("unknown");

        assertFalse(found.isPresent());
        verify(traineeDao).findByUsername("unknown");
    }

    @Test
    void testGetTraineeByUsername_NotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.getTraineeByUsername("john"));

        assertEquals("User not authenticated", ex.getMessage());  // Changed from "Trainee not authenticated"
        verify(traineeDao, never()).findByUsername(anyString());
    }

    @Test
    void testGetTraineeByUsername_DifferentUser() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.getTraineeByUsername("john"));

        assertEquals("User not authenticated", ex.getMessage());  // Changed from "Trainee not authenticated"
        verify(traineeDao, never()).findByUsername(anyString());
    }

    @Test
    void testUpdateTrainee() {
        Trainee updated = new Trainee();
        updated.setUsername("john");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john");

        Trainee result = traineeService.updateTrainee("john", updated);

        verify(traineeDao).update(updated);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testUpdateTrainee_NotAuthenticated() {
        Trainee updated = new Trainee();
        updated.setUsername("john");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.updateTrainee("john", updated));

        assertEquals("User not authenticated", ex.getMessage());
        verify(traineeDao, never()).update(any());
    }

    @Test
    void testDeleteTraineeByUsername_Found() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john");
        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        traineeService.deleteTraineeByUsername("john");

        verify(traineeDao).findByUsername("john");
        verify(traineeDao).delete(trainee);
    }

    @Test
    void testDeleteTraineeByUsername_NotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("unknown");
        when(traineeDao.findByUsername("unknown")).thenReturn(null);

        traineeService.deleteTraineeByUsername("unknown");

        verify(traineeDao).findByUsername("unknown");
        verify(traineeDao, never()).delete(any());
    }

    @Test
    void testDeleteTraineeByUsername_NotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.deleteTraineeByUsername("john"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(traineeDao, never()).findByUsername(anyString());
    }

    @Test
    void testGetAllTrainees() {
        Trainee t1 = new Trainee();
        Trainee t2 = new Trainee();

        when(traineeDao.findAll()).thenReturn(List.of(t1, t2));

        List<Trainee> list = traineeService.getAllTrainees();

        assertEquals(2, list.size());
        verify(traineeDao).findAll();
    }
}