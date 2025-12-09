package org.example.services.impl;

import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceInMemoryImplTest {

    private TraineeDao traineeDao;
    private TraineeServiceInMemoryImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class);
        traineeService = new TraineeServiceInMemoryImpl();
        traineeService.setTraineeDao(traineeDao);
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
    void testGetTraineeByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        Optional<Trainee> found = traineeService.getTraineeByUsername("john");

        assertTrue(found.isPresent());
        assertEquals("john", found.get().getUsername());
    }

    @Test
    void testUpdateTrainee() {
        Trainee updated = new Trainee();
        updated.setUsername("john");

        Trainee result = traineeService.updateTrainee("john", updated);

        verify(traineeDao).update(updated);
        assertEquals("john", result.getUsername());
    }

    @Test
    void testDeleteTraineeByUsername() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        traineeService.deleteTraineeByUsername("john");

        verify(traineeDao).delete(trainee);
    }

    @Test
    void testDeleteTraineeByUsername_NotFound() {
        when(traineeDao.findByUsername("unknown")).thenReturn(null);

        traineeService.deleteTraineeByUsername("unknown");

        verify(traineeDao, never()).delete(any());
    }

    @Test
    void testPasswordMatches_Correct() {
        Trainee trainee = new Trainee();
        trainee.setPassword("123".toCharArray());

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        assertTrue(traineeService.passwordMatches("john", "123".toCharArray()));
    }

    @Test
    void testPasswordMatches_Incorrect() {
        Trainee trainee = new Trainee();
        trainee.setPassword("123".toCharArray());

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        assertFalse(traineeService.passwordMatches("john", "999".toCharArray()));
    }

    @Test
    void testChangePassword() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        char[] newPass = "newpass".toCharArray();

        Trainee result = traineeService.changePassword("john", newPass);

        assertArrayEquals(newPass, result.getPassword());
        verify(traineeDao).update(trainee);
    }

    @Test
    void testSetActiveStatus() {
        Trainee trainee = new Trainee();
        trainee.setUsername("john");

        when(traineeDao.findByUsername("john")).thenReturn(trainee);

        Trainee result = traineeService.setActiveStatus("john", true);

        assertTrue(result.isActive());
        verify(traineeDao).update(trainee);
    }

    @Test
    void testGetAllTrainees() {
        Trainee t1 = new Trainee();
        Trainee t2 = new Trainee();

        when(traineeDao.findAll()).thenReturn(List.of(t1, t2));

        List<Trainee> list = traineeService.getAllTrainees();

        assertEquals(2, list.size());
    }
}
