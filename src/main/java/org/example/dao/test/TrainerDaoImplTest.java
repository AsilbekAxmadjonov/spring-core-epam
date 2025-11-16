package org.example.dao.test;

import org.example.dao.impl.TrainerDaoImpl;
import org.example.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoImplTest {

    private TrainerDaoImpl trainerDao;
    private Map<String, Trainer> trainerStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new HashMap<>();
        trainerDao = new TrainerDaoImpl();
        trainerDao.setTrainerStorage(trainerStorage); // manual setter injection
    }

    private Trainer createTrainer(String username, String firstName, String lastName, String specialization) {
        return Trainer.builder()
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .specialization(specialization)
                .password("secret".toCharArray())
                .isActive(true)
                .build();
    }

    @Test
    void testSaveTrainer() {
        Trainer trainer = createTrainer("trainer01", "John", "Smith", "Backend");
        trainerDao.save(trainer);

        assertEquals(1, trainerStorage.size());
        assertTrue(trainerStorage.containsKey("trainer01"));
        assertEquals(trainer, trainerStorage.get("trainer01"));
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = createTrainer("trainer01", "John", "Smith", "Backend");
        trainerDao.save(trainer);

        trainer.setSpecialization("Frontend");
        trainerDao.update(trainer);

        Trainer updated = trainerStorage.get("trainer01");
        assertEquals("Frontend", updated.getSpecialization());
    }

    @Test
    void testFindByUsername() {
        Trainer trainer = createTrainer("trainer01", "Alice", "Brown", "DevOps");
        trainerDao.save(trainer);

        Trainer found = trainerDao.findByUsername("trainer01");

        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
        assertEquals("DevOps", found.getSpecialization());
    }

    @Test
    void testFindByUsername_NotFound() {
        Trainer result = trainerDao.findByUsername("unknown");
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Trainer t1 = createTrainer("trainer1", "Tom", "Cruise", "Java");
        Trainer t2 = createTrainer("trainer2", "Emma", "Watson", "Python");

        trainerDao.save(t1);
        trainerDao.save(t2);

        List<Trainer> trainers = trainerDao.findAll();

        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(t1));
        assertTrue(trainers.contains(t2));
    }
}
