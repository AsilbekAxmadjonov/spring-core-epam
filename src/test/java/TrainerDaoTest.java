import org.example.dao.TrainerDao;
import org.example.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainerDaoTest {

    private TrainerDao trainerDao;
    private Map<String, Trainer> trainerStorage;

    @BeforeEach
    void setUp() {
        trainerStorage = new HashMap<>();
        trainerDao = new TrainerDao(trainerStorage);
    }

    @Test
    void testSaveTrainer() {
        Trainer trainer = new Trainer("john123", "John", "Doe", "Fitness");
        trainerDao.save(trainer);

        assertEquals(1, trainerStorage.size());
        assertTrue(trainerStorage.containsKey("john123"));
        assertEquals(trainer, trainerStorage.get("john123"));
    }

    @Test
    void testUpdateTrainer() {
        Trainer trainer = new Trainer("john123", "John", "Doe", "Fitness");
        trainerDao.save(trainer);

        trainer.setSpecialization("Yoga");
        trainerDao.update(trainer);

        Trainer updatedTrainer = trainerStorage.get("john123");
        assertEquals("Yoga", updatedTrainer.getSpecialization());
    }

    @Test
    void testFindByUsername() {
        Trainer trainer = new Trainer("alice01", "Alice", "Brown", "Cardio");
        trainerDao.save(trainer);

        Trainer found = trainerDao.findByUsername("alice01");
        assertNotNull(found);
        assertEquals("Alice", found.getFirstName());
    }

    @Test
    void testFindByUsername_NotFound() {
        Trainer result = trainerDao.findByUsername("not_exist");
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Trainer t1 = new Trainer("t1", "John", "Smith", "Fitness");
        Trainer t2 = new Trainer("t2", "Jane", "Doe", "Yoga");

        trainerDao.save(t1);
        trainerDao.save(t2);

        List<Trainer> trainers = trainerDao.findAll();
        assertEquals(2, trainers.size());
        assertTrue(trainers.contains(t1));
        assertTrue(trainers.contains(t2));
    }
}
