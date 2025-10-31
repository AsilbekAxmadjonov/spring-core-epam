import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TraineeDaoTest {

    private TraineeDao traineeDao;
    private Map<String, Trainee> traineeStorage;

    @BeforeEach
    void setUp() {
        traineeStorage = new HashMap<>();
        traineeDao = new TraineeDao(traineeStorage);
    }

    @Test
    void testSaveTrainee() {
        Trainee trainee = new Trainee("john01", "John", "Doe", LocalDate.of(1998, 5, 10), "123 Main St");
        traineeDao.save(trainee);

        assertEquals(1, traineeStorage.size());
        assertTrue(traineeStorage.containsKey("john01"));
        assertEquals(trainee, traineeStorage.get("john01"));
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = new Trainee("john01", "John", "Doe", LocalDate.of(1998, 5, 10), "123 Main St");
        traineeDao.save(trainee);

        // Update address
        trainee.setAddress("456 Elm St");
        traineeDao.update(trainee);

        Trainee updated = traineeStorage.get("john01");
        assertEquals("456 Elm St", updated.getAddress());
    }

    @Test
    void testFindByUsername() {
        Trainee trainee = new Trainee("alice01", "Alice", "Brown", LocalDate.of(2000, 8, 12), "77 Sunset Blvd");
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
        Trainee t1 = new Trainee("t1", "John", "Smith", LocalDate.of(1995, 2, 14), "Address1");
        Trainee t2 = new Trainee("t2", "Jane", "Doe", LocalDate.of(1997, 6, 22), "Address2");

        traineeDao.save(t1);
        traineeDao.save(t2);

        List<Trainee> trainees = traineeDao.findAll();
        assertEquals(2, trainees.size());
        assertTrue(trainees.contains(t1));
        assertTrue(trainees.contains(t2));
    }
}
