package org.example.dao.impl;

import org.example.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoImplTest {

    private TraineeDaoImpl traineeDao;
    private Map<String, Trainee> traineeStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        traineeDao = new TraineeDaoImpl();
        traineeDao.setTraineeStorage(traineeStorage); // manual injection
    }

    private Trainee createTrainee(String username, String first, String last, String address) {
        return Trainee.builder()
                .username(username)
                .firstName(first)
                .lastName(last)
                .password("pass123".toCharArray())
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address(address)
                .build();
    }

    @Test
    void testSaveTrainee() {
        Trainee trainee = createTrainee("john01", "John", "Doe", "123 Main St");
        traineeDao.save(trainee);

        assertEquals(1, traineeStorage.size());
        assertTrue(traineeStorage.containsKey("john01"));
        assertEquals(trainee, traineeStorage.get("john01"));
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = createTrainee("john01", "John", "Doe", "123 Main St");
        traineeDao.save(trainee);

        trainee.setAddress("456 Elm St");
        traineeDao.update(trainee);

        Trainee updated = traineeStorage.get("john01");
        assertEquals("456 Elm St", updated.getAddress());
    }

    @Test
    void testDeleteTrainee() {
        Trainee trainee = createTrainee("john01", "John", "Doe", "123 Main St");
        traineeDao.save(trainee);

        traineeDao.delete(trainee);

        assertFalse(traineeStorage.containsKey("john01"));
        assertTrue(traineeStorage.isEmpty());
    }

    @Test
    void testDeleteTrainee_NotExist() {
        Trainee trainee = createTrainee("ghost01", "Ghost", "User", "Nowhere");
        traineeDao.delete(trainee);

        assertTrue(traineeStorage.isEmpty());
    }

    @Test
    void testFindByUsername() {
        Trainee trainee = createTrainee("alice01", "Alice", "Brown", "77 Sunset Blvd");
        traineeDao.save(trainee);

        Trainee found = traineeDao.findByUsername("alice01");
        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    void testFindByUsername_NotFound() {
        Trainee result = traineeDao.findByUsername("not_exist");
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Trainee t1 = createTrainee("t1", "John", "Smith", "Address1");
        Trainee t2 = createTrainee("t2", "Jane", "Doe", "Address2");

        traineeDao.save(t1);
        traineeDao.save(t2);

        List<Trainee> trainees = traineeDao.findAll();
        assertEquals(2, trainees.size());
        assertTrue(trainees.contains(t1));
        assertTrue(trainees.contains(t2));
    }
}
