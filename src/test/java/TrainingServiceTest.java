import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrainingServiceTest {

    private TrainingDao trainingDao;
    private TrainingService trainingService;

    @BeforeEach
    void setUp() {
        trainingDao = Mockito.mock(TrainingDao.class);
        trainingService = new TrainingService(trainingDao);
    }

    @Test
    void testCreateTraining_ShouldCallSaveOnce() {
        Training training = createSampleTraining();

        trainingService.createTraining(training);

        verify(trainingDao, times(1)).save(training);
    }

    @Test
    void testGetTraining_ShouldReturnCorrectTraining() {
        Training expected = createSampleTraining();
        when(trainingDao.findByName("Spring Boot Basics")).thenReturn(expected);

        Training result = trainingService.getTraining("Spring Boot Basics");

        assertNotNull(result);
        assertEquals("Spring Boot Basics", result.getTrainingName());
        assertEquals(expected, result);
        verify(trainingDao, times(1)).findByName("Spring Boot Basics");
    }

    @Test
    void testListAll_ShouldReturnAllTrainings() {
        List<Training> mockList = List.of(createSampleTraining());
        when(trainingDao.findAll()).thenReturn(mockList);

        List<Training> result = trainingService.listAll();

        assertEquals(1, result.size());
        assertEquals("Spring Boot Basics", result.get(0).getTrainingName());
        verify(trainingDao, times(1)).findAll();
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
