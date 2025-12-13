package org.example.services.impl.dbImpl;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainingMapper;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.repository.TraineeRepo;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingRepo;
import org.example.repository.TrainingTypeRepo;
import org.example.security.AuthenticationContext;
import org.example.services.TrainingService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TrainingServiceDbImplTest {

    private TrainingRepo trainingRepo;
    private TraineeRepo traineeRepo;
    private TrainerRepo trainerRepo;
    private TrainingTypeRepo trainingTypeRepo;
    private TrainingMapper trainingMapper;

    private TrainingService service;

    @BeforeEach
    void setUp() {
        trainingRepo = mock(TrainingRepo.class);
        traineeRepo = mock(TraineeRepo.class);
        trainerRepo = mock(TrainerRepo.class);
        trainingTypeRepo = mock(TrainingTypeRepo.class);
        trainingMapper = mock(TrainingMapper.class);

        service = new TrainingServiceDbImpl(
                trainingRepo,
                traineeRepo,
                trainerRepo,
                trainingMapper,
                trainingTypeRepo
        );
    }

    @AfterEach
    void tearDown() {
        AuthenticationContext.clear();
    }

    @Test
    void testGetTraineeTrainings() {
        AuthenticationContext.setAuthenticatedUser("john"); // ✅ must match traineeUsername

        TrainingEntity entity = new TrainingEntity();
        List<TrainingEntity> entityList = List.of(entity);
        Training model = new Training();
        List<Training> modelList = List.of(model);

        when(trainingRepo.findTraineeTrainings(
                eq("john"),
                any(), any(), any(), any())
        ).thenReturn(entityList);

        when(trainingMapper.toTrainingModels(entityList)).thenReturn(modelList);

        List<Training> result = service.getTraineeTrainings(
                "john",
                LocalDate.now(),
                LocalDate.now(),
                "Alex",
                "Cardio"
        );

        assertEquals(1, result.size());
        verify(trainingRepo).findTraineeTrainings(
                eq("john"), any(), any(), any(), any()
        );
        verify(trainingMapper).toTrainingModels(entityList);
    }

    @Test
    void testGetTrainerTrainings() {
        AuthenticationContext.setAuthenticatedUser("trainer01"); // ✅ must match trainerUsername

        TrainingEntity entity = new TrainingEntity();
        List<TrainingEntity> entityList = List.of(entity);
        Training model = new Training();
        List<Training> modelList = List.of(model);

        when(trainingRepo.findTrainerTrainings(
                eq("trainer01"),
                any(), any(), any())
        ).thenReturn(entityList);

        when(trainingMapper.toTrainingModels(entityList)).thenReturn(modelList);

        List<Training> result = service.getTrainerTrainings(
                "trainer01",
                LocalDate.now(),
                LocalDate.now(),
                "Mike"
        );

        assertEquals(1, result.size());
        verify(trainingRepo).findTrainerTrainings(
                eq("trainer01"), any(), any(), any()
        );
        verify(trainingMapper).toTrainingModels(entityList);
    }

    @Test
    void testAddTrainingSuccess() {
        AuthenticationContext.setAuthenticatedUser("john"); // ✅ must match trainee username

        Training model = Training.builder()
                .traineeUsername("john")
                .trainerUsername("alex")
                .trainingName("Morning Session")
                .trainingType(new TrainingType("Cardio"))
                .trainingDate(LocalDate.now())
                .trainingDurationMinutes(60)
                .build();

        TraineeEntity trainee = new TraineeEntity();
        TrainerEntity trainer = new TrainerEntity();
        TrainingTypeEntity typeEntity = new TrainingTypeEntity(); // ✅ mock training type
        TrainingEntity entity = new TrainingEntity();
        TrainingEntity savedEntity = new TrainingEntity();

        when(traineeRepo.findByUsername("john")).thenReturn(Optional.of(trainee));
        when(trainerRepo.findByUsername("alex")).thenReturn(Optional.of(trainer));
        when(trainingTypeRepo.findByTrainingTypeName("Cardio")).thenReturn(Optional.of(typeEntity)); // ✅ mock type
        when(trainingMapper.toTrainingEntity(model)).thenReturn(entity);
        when(trainingRepo.save(entity)).thenReturn(savedEntity);
        when(trainingMapper.toTrainingModel(savedEntity)).thenReturn(model);

        Training result = service.addTraining(model);

        assertNotNull(result);
        verify(trainingRepo).save(entity);
        verify(trainingMapper).toTrainingModel(savedEntity);
    }

    @Test
    void testAddTrainingTraineeNotFound() {
        AuthenticationContext.setAuthenticatedUser("anyuser");

        Training model = new Training();
        model.setTraineeUsername("unknown");

        when(traineeRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                service.addTraining(model)
        );
    }

    @Test
    void testAddTrainingTrainerNotFound() {
        AuthenticationContext.setAuthenticatedUser("john");

        Training model = new Training();
        model.setTraineeUsername("john");
        model.setTrainerUsername("unknown");

        TraineeEntity trainee = new TraineeEntity();

        when(traineeRepo.findByUsername("john"))
                .thenReturn(Optional.of(trainee));
        when(trainerRepo.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                service.addTraining(model)
        );
    }

    @Test
    void testGetTrainingSuccess() {
        AuthenticationContext.setAuthenticatedUser("john");

        String trainingName = "YogaSession";
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findByTrainingName(trainingName)).thenReturn(Optional.of(entity));
        when(trainingMapper.toTrainingModel(entity)).thenReturn(model);

        Training result = service.getTraining(trainingName);

        assertNotNull(result);
        verify(trainingRepo).findByTrainingName(trainingName);
        verify(trainingMapper).toTrainingModel(entity);
    }

    @Test
    void testGetTrainingNotFound() {
        AuthenticationContext.setAuthenticatedUser("john");

        String trainingName = "Unknown";
        when(trainingRepo.findByTrainingName(trainingName)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.getTraining(trainingName));
    }

    @Test
    void testListAllTrainings() {
        TrainingEntity entity = new TrainingEntity();
        Training model = new Training();

        when(trainingRepo.findAll()).thenReturn(List.of(entity));
        when(trainingMapper.toTrainingModels(List.of(entity))).thenReturn(List.of(model));

        List<Training> result = service.listAll();

        assertEquals(1, result.size());
        verify(trainingRepo).findAll();
        verify(trainingMapper).toTrainingModels(List.of(entity));
    }
}
