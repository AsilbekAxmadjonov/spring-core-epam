import org.example.dao.TraineeDao;
import org.example.model.Trainee;
import org.example.service.TraineeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TraineeServiceTest {

    @Mock
    private TraineeDao traineeDao;

    @InjectMocks
    private TraineeService traineeService;

    private Trainee trainee;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        trainee = new Trainee(
                "john_doe",
                "John",
                "Doe",
                LocalDate.of(1995, 5, 15),
                "123 Main St"
        );
    }

    @Test
    void testCreateTrainee() {
        traineeService.createTrainee(trainee);
        verify(traineeDao, times(1)).save(trainee);
    }

    @Test
    void testUpdateTrainee() {
        traineeService.updateTrainee(trainee);
        verify(traineeDao, times(1)).update(trainee);
    }

    @Test
    void testGetTrainee() {
        when(traineeDao.findByUsername("john_doe")).thenReturn(trainee);

        Trainee result = traineeService.getTrainee("john_doe");

        assertNotNull(result);
        assertEquals("John", result.getFirstName());
        verify(traineeDao, times(1)).findByUsername("john_doe");
    }

    @Test
    void testListAll() {
        when(traineeDao.findAll()).thenReturn(List.of(trainee));

        List<Trainee> trainees = traineeService.listAll();

        assertEquals(1, trainees.size());
        assertEquals("john_doe", trainees.get(0).getUsername());
        verify(traineeDao, times(1)).findAll();
    }
}
