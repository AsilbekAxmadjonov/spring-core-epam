package org.example.services.impl.inMemoryImpl;

import org.example.api.dto.request.TrainingRequest;
import org.example.dao.TrainingDao;
import org.example.persistance.model.Training;
import org.example.persistance.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

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

    private TrainingRequest createTrainingRequest(String name, String trainee, String trainer, String type, LocalDate date, int duration) {
        return TrainingRequest.builder()
                .trainingName(name)
                .traineeUsername(trainee)
                .trainerUsername(trainer)
                .trainingType(type)
                .trainingDate(date)
                .trainingDurationMinutes(duration)
                .build();
    }

    @Test
    void testCreateTraining() {
        TrainingRequest request = createTrainingRequest(
                "Spring Boot Basics", "trainee01", "trainer01", "Backend",
                LocalDate.of(2025, 2, 1), 90
        );

        Training expectedTraining = Training.builder()
                .trainingName(request.getTrainingName())
                .traineeUsername(request.getTraineeUsername())
                .trainerUsername(request.getTrainerUsername())
                .trainingType(new TrainingType(request.getTrainingType()))
                .trainingDate(request.getTrainingDate())
                .trainingDurationMinutes(request.getTrainingDurationMinutes())
                .build();

        trainingService.createTraining(request);

        // Verify that DAO save was called with the converted Training
        ArgumentCaptor<Training> captor = ArgumentCaptor.forClass(Training.class);
        verify(trainingDao, times(1)).save(captor.capture());
        Training captured = captor.getValue();

        assertEquals(expectedTraining.getTrainingName(), captured.getTrainingName());
        assertEquals(expectedTraining.getTraineeUsername(), captured.getTraineeUsername());
        assertEquals(expectedTraining.getTrainerUsername(), captured.getTrainerUsername());
        assertEquals(expectedTraining.getTrainingType().getTrainingTypeName(), captured.getTrainingType().getTrainingTypeName());
    }

    @Test
    void testGetTraining() {
        Training training = Training.builder()
                .trainingName("Python for Data")
                .traineeUsername("trainee02")
                .trainerUsername("trainer02")
                .trainingType(new TrainingType("Data Science"))
                .trainingDate(LocalDate.of(2025, 3, 15))
                .trainingDurationMinutes(60)
                .build();

        when(trainingDao.findByName("Python for Data")).thenReturn(training);

        Training result = trainingService.getTraining("Python for Data");

        assertNotNull(result);
        assertEquals("trainer02", result.getTrainerUsername());
        assertEquals("Data Science", result.getTrainingType().getTrainingTypeName());

        verify(trainingDao, times(1)).findByName("Python for Data");
    }

    @Test
    void testGetTraining_notFound() {
        when(trainingDao.findByName("Unknown")).thenReturn(null);

        assertThrows(NoSuchElementException.class, () -> trainingService.getTraining("Unknown"));
    }

    @Test
    void testListAll() {
        Training t1 = Training.builder()
                .trainingName("Training 1")
                .traineeUsername("trainee1")
                .trainerUsername("trainer1")
                .trainingType(new TrainingType("Backend"))
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(45)
                .build();

        Training t2 = Training.builder()
                .trainingName("Training 2")
                .traineeUsername("trainee2")
                .trainerUsername("trainer2")
                .trainingType(new TrainingType("Frontend"))
                .trainingDate(LocalDate.now().plusDays(1))
                .trainingDurationMinutes(60)
                .build();

        when(trainingDao.findAll()).thenReturn(List.of(t1, t2));

        List<Training> trainings = trainingService.listAll();

        assertEquals(2, trainings.size());
        assertTrue(trainings.contains(t1));
        assertTrue(trainings.contains(t2));

        verify(trainingDao, times(1)).findAll();
    }

    @Test
    void testGetTraineeTrainings_unsupported() {
        assertThrows(UnsupportedOperationException.class, () ->
                trainingService.getTraineeTrainings("trainee01", null, null, null, null));
    }

    @Test
    void testGetTrainerTrainings_unsupported() {
        assertThrows(UnsupportedOperationException.class, () ->
                trainingService.getTrainerTrainings("trainer01", null, null, null));
    }
}
