package org.example.services.impl.dbImpl;

import org.example.entity.*;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.repository.*;
import org.example.services.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainingServiceDbImplTest {

    @Mock
    private TrainingRepo trainingRepo;

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private TrainingServiceDbImpl service;

    @Test
    void getTraineeTrainings_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findTraineeTrainings(
                eq("john"), any(), any(), any(), any()
        )).thenReturn(List.of(entity));

        when(trainingMapper.toTrainingModels(List.of(entity)))
                .thenReturn(List.of(model));

        List<Training> result = service.getTraineeTrainings(
                "john",
                LocalDate.now(),
                LocalDate.now(),
                "Alex",
                "Cardio"
        );

        assertEquals(1, result.size());
        verify(trainingRepo).findTraineeTrainings(eq("john"), any(), any(), any(), any());
        verify(trainingMapper).toTrainingModels(anyList());
    }

    @Test
    void getTrainerTrainings_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findTrainerTrainings(
                eq("trainer01"), any(), any(), any()
        )).thenReturn(List.of(entity));

        when(trainingMapper.toTrainingModels(List.of(entity)))
                .thenReturn(List.of(model));

        List<Training> result = service.getTrainerTrainings(
                "trainer01",
                LocalDate.now(),
                LocalDate.now(),
                "Mike"
        );

        assertEquals(1, result.size());
        verify(trainingRepo).findTrainerTrainings(eq("trainer01"), any(), any(), any());
        verify(trainingMapper).toTrainingModels(anyList());
    }

    @Test
    void addTraining_success() {
        Training training = Training.builder()
                .traineeUsername("john")
                .trainerUsername("alex")
                .trainingName("Morning Session")
                .trainingType(new TrainingType("Cardio"))
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        TraineeEntity trainee = new TraineeEntity();
        TrainerEntity trainer = new TrainerEntity();
        TrainingTypeEntity typeEntity = new TrainingTypeEntity();
        TrainingEntity entity = new TrainingEntity();
        TrainingEntity savedEntity = new TrainingEntity();

        when(traineeRepo.findByUsername("john"))
                .thenReturn(Optional.of(trainee));
        when(trainerRepo.findByUsername("alex"))
                .thenReturn(Optional.of(trainer));
        when(trainingTypeRepo.findByTrainingTypeName("Cardio"))
                .thenReturn(Optional.of(typeEntity));
        when(trainingMapper.toTrainingEntity(training))
                .thenReturn(entity);
        when(trainingRepo.save(entity))
                .thenReturn(savedEntity);
        when(trainingMapper.toTrainingModel(savedEntity))
                .thenReturn(training);

        Training result = service.addTraining(training);

        assertNotNull(result);
        verify(trainingRepo).save(entity);
        verify(trainingMapper).toTrainingModel(savedEntity);
    }

    @Test
    void addTraining_traineeNotFound() {
        Training training = new Training();
        training.setTraineeUsername("unknown");

        when(traineeRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.addTraining(training));
    }

    @Test
    void addTraining_trainerNotFound() {
        Training training = new Training();
        training.setTraineeUsername("john");
        training.setTrainerUsername("unknown");
        training.setTrainingType(new TrainingType("Cardio"));

        when(traineeRepo.findByUsername("john"))
                .thenReturn(Optional.of(new TraineeEntity()));
        when(trainerRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.addTraining(training));
    }

    @Test
    void getTraining_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findByTrainingName("Yoga"))
                .thenReturn(Optional.of(entity));
        when(trainingMapper.toTrainingModel(entity))
                .thenReturn(model);

        Training result = service.getTraining("Yoga");

        assertNotNull(result);
        verify(trainingRepo).findByTrainingName("Yoga");
    }

    @Test
    void getTraining_notFound() {
        when(trainingRepo.findByTrainingName("Unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getTraining("Unknown"));
    }

    @Test
    void listAll_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findAll())
                .thenReturn(List.of(entity));
        when(trainingMapper.toTrainingModels(List.of(entity)))
                .thenReturn(List.of(model));

        List<Training> result = service.listAll();

        assertEquals(1, result.size());
        verify(trainingRepo).findAll();
    }
}
