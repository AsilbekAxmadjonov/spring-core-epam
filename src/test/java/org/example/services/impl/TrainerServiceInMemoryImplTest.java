package org.example.services.impl;

import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainerServiceInMemoryImplTest {

    private TrainerDao trainerDao;
    private TrainerServiceInMemoryImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerDao = mock(TrainerDao.class); // mock dependency
        trainerService = new TrainerServiceInMemoryImpl();
        trainerService.setTrainerDao(trainerDao); // inject mock
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
    void testGetTrainerByUsername() {
        Trainer trainer = createTrainer("alice01", "Alice", "Brown");
        when(trainerDao.findByUsername("alice01")).thenReturn(trainer);

        Optional<Trainer> result = trainerService.getTrainerByUsername("alice01");

        assertTrue(result.isPresent());
        assertEquals("Alice", result.get().getFirstName());
        verify(trainerDao, times(1)).findByUsername("alice01");
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer("bob01", "Bob", "Smith");

        Trainer result = trainerService.updateTrainer("bob01", trainer);

        verify(trainerDao, times(1)).update(trainer);
        assertEquals(trainer, result);
    }

    @Test
    void testPasswordMatches() {
        Trainer trainer = createTrainer("charlie01", "Charlie", "Jones");
        when(trainerDao.findByUsername("charlie01")).thenReturn(trainer);

        boolean match = trainerService.passwordMatches("charlie01", "123".toCharArray());
        boolean notMatch = trainerService.passwordMatches("charlie01", "456".toCharArray());

        assertTrue(match);
        assertFalse(notMatch);
        verify(trainerDao, times(2)).findByUsername("charlie01");
    }

    @Test
    void testChangePassword() {
        Trainer trainer = createTrainer("dave01", "Dave", "Miller");
        when(trainerDao.findByUsername("dave01")).thenReturn(trainer);

        char[] newPassword = "456".toCharArray();
        Trainer result = trainerService.changePassword("dave01", newPassword);

        verify(trainerDao, times(1)).update(trainer);
        assertArrayEquals(newPassword, result.getPassword());
    }

    @Test
    void testChangePassword_TrainerNotFound() {
        when(trainerDao.findByUsername("unknown")).thenReturn(null);

        assertThrows(NoSuchElementException.class,
                () -> trainerService.changePassword("unknown", "123".toCharArray()));
    }

    @Test
    void testSetActiveStatus() {
        Trainer trainer = createTrainer("eve01", "Eve", "White");
        when(trainerDao.findByUsername("eve01")).thenReturn(trainer);

        Trainer result = trainerService.setActiveStatus("eve01", false);

        verify(trainerDao, times(1)).update(trainer);
        assertFalse(result.isActive());
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

    @Test
    void testGetTrainerByUsername_NotFound() {
        when(trainerDao.findByUsername("unknown")).thenReturn(null);

        Optional<Trainer> result = trainerService.getTrainerByUsername("unknown");

        assertTrue(result.isEmpty());
    }
}
