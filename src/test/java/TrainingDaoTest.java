import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoTest {

    private Map<String, Training> trainingStorage;
    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        trainingStorage = new HashMap<>();
        trainingDao = new TrainingDao(trainingStorage);
    }

    @Test
    void testSaveAndFindByName() {
        Training training = createSampleTraining();

        trainingDao.save(training);
        Training found = trainingDao.findByName("Spring Boot Basics");

        assertNotNull(found);
        assertEquals("Spring Boot Basics", found.getTrainingName());
        assertEquals(training.getTrainerId(), found.getTrainerId());
    }

    @Test
    void testFindByName_NotFound() {
        Training result = trainingDao.findByName("NonExistent");
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Training t1 = createSampleTraining();
        Training t2 = new Training(
                "trainee2",
                "trainer2",
                "Advanced Java",
                new TrainingType("Strength"),
                LocalDate.of(2025, 2, 20),
                30
        );

        trainingDao.save(t1);
        trainingDao.save(t2);

        List<Training> all = trainingDao.findAll();

        assertEquals(2, all.size());
        assertTrue(all.contains(t1));
        assertTrue(all.contains(t2));
    }

    private Training createSampleTraining() {
        return new Training(
                "trainee1",
                "trainer1",
                "Spring Boot Basics",
                new TrainingType("Cardio"),
                LocalDate.of(2025, 1, 10),
                15
        );
    }
}
