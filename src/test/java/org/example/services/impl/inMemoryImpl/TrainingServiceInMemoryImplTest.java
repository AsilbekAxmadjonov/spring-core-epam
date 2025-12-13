package org.example.services.impl.inMemoryImpl;

import org.example.dao.TrainingDao;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceInMemoryImplTest {

    private TrainingDao trainingDao;
    private TrainingServiceInMemoryImpl trainingService;

    @BeforeEach
    void setUp() {
        trainingDao = mock(TrainingDao.class);
        trainingService = new TrainingServiceInMemoryImpl();
        trainingService.setTrainingDao(trainingDao);
    }

    private Training createTraining(String name, String trainee, String trainer, String type, LocalDate date, int duration) {
        return Training.builder()
                .trainingName(name)
                .traineeUsername(trainee)
                .trainerUsername(trainer)
                .trainingType(new TrainingType(type))
                .trainingDate(date)
                .trainingDurationMinutes(duration)
                .build();
    }

    @Test
    void testCreateTraining() {
        Training training = createTraining("Spring Boot Basics", "trainee01", "trainer01", "Backend",
                LocalDate.of(2025, 2, 1), 90);

        trainingService.createTraining(training);

        verify(trainingDao, times(1)).save(training);
    }

    @Test
    void testGetTraining() {
        Training training = createTraining("Python for Data", "trainee02", "trainer02", "Data Science",
                LocalDate.of(2025, 3, 15), 60);

        when(trainingDao.findByName("Python for Data")).thenReturn(training);

        Training result = trainingService.getTraining("Python for Data");

        assertNotNull(result);
        assertEquals("trainer02", result.getTrainerUsername());
        assertEquals("Data Science", result.getTrainingType().getTrainingTypeName());

        verify(trainingDao, times(1)).findByName("Python for Data");
    }

    @Test
    void testListAll() {
        Training t1 = createTraining("Training 1", "trainee1", "trainer1", "Backend",
                LocalDate.now(), 45);
        Training t2 = createTraining("Training 2", "trainee2", "trainer2", "Frontend",
                LocalDate.now().plusDays(1), 60);

        when(trainingDao.findAll()).thenReturn(List.of(t1, t2));

        List<Training> trainings = trainingService.listAll();

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(t1));
        assertTrue(trainings.contains(t2));

        verify(trainingDao, times(1)).findAll();
    }

    @Test
    void testCreateTraining_ArgumentPassedCorrectly() {
        Training training = createTraining("AWS Cloud Basics", "trainee03", "trainer03", "Cloud",
                LocalDate.of(2025, 5, 20), 120);

        trainingService.createTraining(training);

        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao).save(captor.capture());

        Training captured = captor.getValue();
        assertEquals("AWS Cloud Basics", captured.getTrainingName());
        assertEquals("Cloud", captured.getTrainingType().getTrainingTypeName());
    }
}
