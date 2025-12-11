package org.example.services.impl.inMemoryImpl;

import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceInMemoryImplTest {

    private TraineeDao traineeDao;
    private AuthenticationService authenticationService;
    private TraineeServiceInMemoryImpl traineeService;

    private final char[] dummyPassword = "dummyPass".toCharArray();

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        authenticationService = mock(AuthenticationService.class);

        traineeService = new TraineeServiceInMemoryImpl();
        traineeService.setTraineeDao(traineeDao);
        traineeService.setAuthenticationService(authenticationService);
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

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        Optional<Trainee> found = traineeService.getTraineeByUsername("john", dummyPassword);

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());

        verify(authenticationService).authenticate("john", dummyPassword);
        verify(traineeDao).findByUsername("john");
    }

    @Test
    void testGetTraineeByUsername_NotFound() {
        when(traineeDao.findByUsername("unknown")).thenReturn(null);

        Optional<Trainee> found = traineeService.getTraineeByUsername("unknown", dummyPassword);

        assertFalse(found.isPresent());

        verify(authenticationService).authenticate("unknown", dummyPassword);
        verify(traineeDao).findByUsername("unknown");
    }

    @Test
    void testUpdateTrainee() {
        Trainee updated = new Trainee();
        updated.setUsername("john");

        Trainee result = traineeService.updateTrainee("john", dummyPassword, updated);

        verify(authenticationService).authenticate("john", dummyPassword);
        verify(traineeDao).update(updated);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testDeleteTraineeByUsername_Found() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        traineeService.deleteTraineeByUsername("john", dummyPassword);

        verify(authenticationService).authenticate("john", dummyPassword);
        verify(traineeDao).delete(trainee);
    }

    @Test
    void testDeleteTraineeByUsername_NotFound() {
        when(traineeDao.findByUsername("unknown")).thenReturn(null);

        traineeService.deleteTraineeByUsername("unknown", dummyPassword);

        verify(authenticationService).authenticate("unknown", dummyPassword);
        verify(traineeDao, never()).delete(any());
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
