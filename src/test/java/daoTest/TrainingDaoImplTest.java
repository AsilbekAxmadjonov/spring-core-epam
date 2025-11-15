package daoTest;

import org.example.dao.impl.TrainingDaoImpl;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TrainingDaoImplTest {

    private TrainingDaoImpl trainingDao;
    private Map<String, Training> trainingStorage;

    @BeforeEach
    void setUp() {
        trainingStorage = new HashMap<>();
        trainingDao = new TrainingDaoImpl();
        trainingDao.setTrainingStorage(trainingStorage); // manually inject storage
    }

    private Training createTraining(String name, String trainer, String trainee, String typeName, LocalDate date, int duration) {
        return Training.builder()
                .trainingName(name)
                .trainerUsername(trainer)
                .traineeUsername(trainee)
                .trainingType(new TrainingType(typeName))
                .trainingDate(date)
                .trainingDurationMinutes(duration)
                .build();
    }

    @Test
    void testSaveTraining() {
        Training training = createTraining(
                "Spring Boot Basics",
                "trainer01",
                "trainee01",
                "Backend",
                LocalDate.of(2025, 2, 1),
                60
        );

        trainingDao.save(training);

        assertEquals(1, trainingStorage.size());
        assertTrue(trainingStorage.containsKey("Spring Boot Basics"));
        assertEquals(training, trainingStorage.get("Spring Boot Basics"));
    }

    @Test
    void testFindByName() {
        Training training = createTraining(
                "React Advanced",
                "trainer02",
                "trainee02",
                "Frontend",
                LocalDate.of(2025, 3, 10),
                90
        );

        trainingDao.save(training);

        Training found = trainingDao.findByName("React Advanced");

        assertNotNull(found);
        assertEquals("trainer02", found.getTrainerUsername());
        assertEquals("Frontend", found.getTrainingType().getTrainingTypeName());
    }

    @Test
    void testFindByName_NotFound() {
        Training result = trainingDao.findByName("Unknown Training");
        assertNull(result);
    }

    @Test
    void testFindAll() {
        Training t1 = createTraining(
                "Python Data",
                "trainerA",
                "traineeA",
                "Data Science",
                LocalDate.of(2025, 1, 20),
                75
        );

        Training t2 = createTraining(
                "Java Masterclass",
                "trainerB",
                "traineeB",
                "Backend",
                LocalDate.of(2025, 4, 12),
                120
        );

        trainingDao.save(t1);
        trainingDao.save(t2);

        List<Training> trainings = trainingDao.findAll();

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(t1));
        assertTrue(trainings.contains(t2));
    }
}
