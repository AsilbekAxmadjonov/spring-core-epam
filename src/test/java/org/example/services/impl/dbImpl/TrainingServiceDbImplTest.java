package org.example.services.impl.dbImpl;

import org.example.api.dto.request.TrainingRequest;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.persistance.entity.TraineeEntity;
import org.example.persistance.entity.TrainerEntity;
import org.example.persistance.entity.TrainingEntity;
import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.model.Training;
import org.example.persistance.model.TrainingType;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingRepo;
import org.example.persistance.repository.TrainingTypeRepo;
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
    void createTraining_success() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("john")
                .trainerUsername("alex")
                .trainingName("Morning Session")
                .trainingType("Cardio")
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        TraineeEntity trainee = new TraineeEntity();
        TrainerEntity trainer = new TrainerEntity();
        TrainingTypeEntity typeEntity = new TrainingTypeEntity();
        TrainingEntity entity = new TrainingEntity();
        TrainingEntity savedEntity = new TrainingEntity();
        Training training = Training.builder()
                .traineeUsername("john")
                .trainerUsername("alex")
                .trainingName("Morning Session")
                .trainingType(new TrainingType("Cardio"))
                .trainingDate(request.getTrainingDate())
                .trainingDurationMinutes(60)
                .build();

        when(traineeRepo.findByUsername("john")).thenReturn(Optional.of(trainee));
        when(trainerRepo.findByUsername("alex")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepo.findByTrainingTypeName("Cardio")).thenReturn(Optional.of(typeEntity));
        when(trainingRepo.save(any(TrainingEntity.class))).thenReturn(savedEntity);
        when(trainingMapper.toTrainingModel(savedEntity)).thenReturn(training);

        Training result = service.createTraining(request);

        assertNotNull(result);
        assertEquals("Morning Session", result.getTrainingName());
        verify(trainingRepo).save(any());
        verify(trainingMapper).toTrainingModel(savedEntity);
    }

    @Test
    void createTraining_traineeNotFound() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("unknown")
                .trainerUsername("alex")
                .trainingType("Cardio")
                .build();

        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createTraining(request));
    }

    @Test
    void createTraining_trainerNotFound() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("john")
                .trainerUsername("unknown")
                .trainingType("Cardio")
                .build();

        when(traineeRepo.findByUsername("john")).thenReturn(Optional.of(new TraineeEntity()));
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.createTraining(request));
    }

    @Test
    void createTraining_invalidTrainingType() {
        TrainingRequest request = TrainingRequest.builder()
                .traineeUsername("john")
                .trainerUsername("alex")
                .trainingType("InvalidType")
                .build();

        when(traineeRepo.findByUsername("john")).thenReturn(Optional.of(new TraineeEntity()));
        when(trainerRepo.findByUsername("alex")).thenReturn(Optional.of(new TrainerEntity()));
        when(trainingTypeRepo.findByTrainingTypeName("InvalidType")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.createTraining(request));
    }

    @Test
    void getTraining_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findByTrainingName("Yoga")).thenReturn(Optional.of(entity));
        when(trainingMapper.toTrainingModel(entity)).thenReturn(model);

        Training result = service.getTraining("Yoga");

        assertNotNull(result);
        verify(trainingRepo).findByTrainingName("Yoga");
    }

    @Test
    void getTraining_notFound() {
        when(trainingRepo.findByTrainingName("Unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getTraining("Unknown"));
    }

    @Test
    void listAll_success() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findAll()).thenReturn(List.of(entity));
        when(trainingMapper.toTrainingModels(List.of(entity))).thenReturn(List.of(model));

        List<Training> result = service.listAll();

        assertEquals(1, result.size());
        verify(trainingRepo).findAll();
    }
}
