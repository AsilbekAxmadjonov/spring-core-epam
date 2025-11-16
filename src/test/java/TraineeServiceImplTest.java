import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.service.impl.TraineeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceImplTest {

    private TraineeDao traineeDao;
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeDao = mock(TraineeDao.class); // mock dependency
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeDao(traineeDao); // inject mock
    }

    private Trainee createTrainee(String username, String first, String last, String address) {
        return Trainee.builder()
                .username(username)
                .firstName(first)
                .lastName(last)
                .password("123".toCharArray())
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address(address)
                .build();
    }

    @Test
    void testCreateTrainee() {
        Trainee trainee = createTrainee("john01", "John", "Doe", "123 Main St");

        traineeService.createTrainee(trainee);

        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    void testUpdateTrainee() {
        Trainee trainee = createTrainee("john02", "Johnny", "Smith", "456 Elm St");

        traineeService.updateTrainee(trainee);

        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void testDeleteTrainee() {
        Trainee trainee = createTrainee("john03", "Jack", "Black", "789 Pine Rd");

        traineeService.deleteTrainee(trainee);

        verify(traineeDao, times(1)).delete(trainee);
    }

    @Test
    void testGetTrainee() {
        Trainee trainee = createTrainee("alice01", "Alice", "Brown", "Sunset Blvd");
        when(traineeDao.findByUsername("alice01")).thenReturn(trainee);

        Trainee result = traineeService.getTrainee("alice01");

        assertNotNull(result);
        assertEquals("Alice", result.getFirstName());
        verify(traineeDao, times(1)).findByUsername("alice01");
    }

    @Test
    void testListAll() {
        Trainee t1 = createTrainee("t1", "Tom", "Jones", "Addr1");
        Trainee t2 = createTrainee("t2", "Jane", "Miller", "Addr2");

        when(traineeDao.findAll()).thenReturn(List.of(t1, t2));

        List<Trainee> trainees = traineeService.listAll();

        assertEquals(2, trainees.size());
        assertTrue(trainees.contains(t1));
        assertTrue(trainees.contains(t2));

        verify(traineeDao, times(1)).findAll();
    }

    @Test
    void testCreateTrainee_ArgumentPassedCorrectly() {
        Trainee trainee = createTrainee("check01", "Check", "User", "Test St");

        traineeService.createTrainee(trainee);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).save(captor.capture());

        assertEquals("check01", captor.getValue().getUsername());
    }
}
