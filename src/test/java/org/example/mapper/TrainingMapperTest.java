package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.TrainerEntity;
import org.example.entity.TrainingEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class TrainingMapperTest {

    private TrainingMapper trainingMapper;

    @Mock
    private TrainingTypeMapper trainingTypeMapper;

    private TrainingEntity trainingEntity;
    private Training trainingModel;
    private UserEntity traineeUser;
    private UserEntity trainerUser;
    private TraineeEntity traineeEntity;
    private TrainerEntity trainerEntity;
    private TrainingTypeEntity trainingTypeEntity;

    @BeforeEach
    void setUp() throws Exception {
        trainingMapper = new TrainingMapperImpl();

        injectMockMapper(trainingMapper, "trainingTypeMapper", trainingTypeMapper);

        traineeUser = new UserEntity();
        traineeUser.setId(1L);
        traineeUser.setUsername("john.doe");
        traineeUser.setFirstName("John");
        traineeUser.setLastName("Doe");
        traineeUser.setIsActive(true);

        trainerUser = new UserEntity();
        trainerUser.setId(2L);
        trainerUser.setUsername("jane.smith");
        trainerUser.setFirstName("Jane");
        trainerUser.setLastName("Smith");
        trainerUser.setIsActive(true);

        trainingTypeEntity = new TrainingTypeEntity();
        trainingTypeEntity.setId(1L);
        trainingTypeEntity.setTrainingTypeName("Yoga");

        TrainingType trainingType = new TrainingType("Yoga");

        traineeEntity = new TraineeEntity();
        traineeEntity.setId(1L);
        traineeEntity.setUserEntity(traineeUser);
        traineeEntity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        traineeEntity.setAddress("123 Main St");

        trainerEntity = new TrainerEntity();
        trainerEntity.setId(1L);
        trainerEntity.setUserEntity(trainerUser);
        trainerEntity.setSpecialization(trainingTypeEntity);

        trainingEntity = TrainingEntity.builder()
                .id(1L)
                .traineeEntity(traineeEntity)
                .trainerEntity(trainerEntity)
                .trainingName("Morning Yoga Session")
                .trainingTypeEntity(trainingTypeEntity)
                .trainingDate(LocalDate.of(2024, 12, 1))
                .trainingDurationMinutes(60)
                .build();

        trainingModel = Training.builder()
                .traineeUsername("john.doe")
                .trainerUsername("jane.smith")
                .trainingName("Morning Yoga Session")
                .trainingType(trainingType)
                .trainingDate(LocalDate.of(2024, 12, 1))
                .trainingDurationMinutes(60)
                .build();

        setupTrainingTypeMapperMocks();
    }

    private void injectMockMapper(Object target, String fieldName, Object mock) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, mock);
    }

    private void setupTrainingTypeMapperMocks() {
        lenient().when(trainingTypeMapper.toModel(any(TrainingTypeEntity.class)))
                .thenAnswer(invocation -> {
                    TrainingTypeEntity entity = invocation.getArgument(0);
                    if (entity == null) return null;
                    return new TrainingType(entity.getTrainingTypeName());
                });

        lenient().when(trainingTypeMapper.toEntity(any(TrainingType.class)))
                .thenAnswer(invocation -> {
                    TrainingType model = invocation.getArgument(0);
                    if (model == null) return null;
                    TrainingTypeEntity entity = new TrainingTypeEntity();
                    entity.setTrainingTypeName(model.getTrainingTypeName());
                    return entity;
                });
    }

    @Test
    void testToTrainingModel_Success() {
        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result);
        assertEquals(trainingEntity.getTrainingName(), result.getTrainingName());
        assertEquals(trainingEntity.getTrainingDate(), result.getTrainingDate());
        assertEquals(trainingEntity.getTrainingDurationMinutes(), result.getTrainingDurationMinutes());
        assertEquals("john.doe", result.getTraineeUsername());
        assertEquals("jane.smith", result.getTrainerUsername());

        assertNotNull(result.getTrainingType());
        assertEquals("Yoga", result.getTrainingType().getTrainingTypeName());
    }

    @Test
    void testToTrainingModel_WithNullEntity() {
        Training result = trainingMapper.toTrainingModel(null);

        assertNull(result);
    }

    @Test
    void testToTrainingModel_WithNullTrainee() {
        trainingEntity.setTraineeEntity(null);

        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result);
        assertNull(result.getTraineeUsername());
        assertEquals(trainingEntity.getTrainingName(), result.getTrainingName());
    }

    @Test
    void testToTrainingModel_WithNullTrainer() {
        trainingEntity.setTrainerEntity(null);

        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result);
        assertNull(result.getTrainerUsername());
        assertEquals(trainingEntity.getTrainingName(), result.getTrainingName());
    }

    @Test
    void testToTrainingModel_WithNullTrainingType() {
        trainingEntity.setTrainingTypeEntity(null);

        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result);
        assertNull(result.getTrainingType());
    }

    @Test
    void testToTrainingEntity_Success() {
        TrainingEntity result = trainingMapper.toTrainingEntity(trainingModel);

        assertNotNull(result);
        assertEquals(trainingModel.getTrainingName(), result.getTrainingName());
        assertEquals(trainingModel.getTrainingDate(), result.getTrainingDate());
        assertEquals(trainingModel.getTrainingDurationMinutes(), result.getTrainingDurationMinutes());

        assertNotNull(result.getTrainingTypeEntity());
        assertEquals("Yoga", result.getTrainingTypeEntity().getTrainingTypeName());
    }

    @Test
    void testToTrainingEntity_WithNullModel() {
        TrainingEntity result = trainingMapper.toTrainingEntity(null);

        assertNull(result);
    }

    @Test
    void testToTrainingModels_Success() {
        TrainingEntity training2 = TrainingEntity.builder()
                .id(2L)
                .traineeEntity(traineeEntity)
                .trainerEntity(trainerEntity)
                .trainingName("Evening Yoga Session")
                .trainingTypeEntity(trainingTypeEntity)
                .trainingDate(LocalDate.of(2024, 12, 2))
                .trainingDurationMinutes(90)
                .build();

        List<TrainingEntity> entities = Arrays.asList(trainingEntity, training2);

        List<Training> result = trainingMapper.toTrainingModels(entities);

        assertNotNull(result);
        assertEquals(2, result.size());

        Training first = result.get(0);
        assertEquals(trainingEntity.getTrainingName(), first.getTrainingName());
        assertEquals(60, first.getTrainingDurationMinutes());
        assertEquals("john.doe", first.getTraineeUsername());
        assertEquals("jane.smith", first.getTrainerUsername());

        Training second = result.get(1);
        assertEquals(training2.getTrainingName(), second.getTrainingName());
        assertEquals(90, second.getTrainingDurationMinutes());
        assertEquals("john.doe", second.getTraineeUsername());
        assertEquals("jane.smith", second.getTrainerUsername());
    }

    @Test
    void testToTrainingModels_WithEmptyList() {
        List<Training> result = trainingMapper.toTrainingModels(Arrays.asList());

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToTrainingModels_WithNullList() {
        List<Training> result = trainingMapper.toTrainingModels(null);

        assertNull(result);
    }

    @Test
    void testMappingPreservesAllFields() {
        Training model = trainingMapper.toTrainingModel(trainingEntity);

        assertEquals("Morning Yoga Session", model.getTrainingName());
        assertEquals(LocalDate.of(2024, 12, 1), model.getTrainingDate());
        assertEquals(60, model.getTrainingDurationMinutes());
        assertEquals("john.doe", model.getTraineeUsername());
        assertEquals("jane.smith", model.getTrainerUsername());
        assertNotNull(model.getTrainingType());
        assertEquals("Yoga", model.getTrainingType().getTrainingTypeName());
    }

    @Test
    void testRoundTripMapping() {
        Training model = trainingMapper.toTrainingModel(trainingEntity);
        TrainingEntity entityAgain = trainingMapper.toTrainingEntity(model);

        assertEquals(trainingEntity.getTrainingName(), entityAgain.getTrainingName());
        assertEquals(trainingEntity.getTrainingDate(), entityAgain.getTrainingDate());
        assertEquals(trainingEntity.getTrainingDurationMinutes(), entityAgain.getTrainingDurationMinutes());
        assertEquals(trainingEntity.getTrainingTypeEntity().getTrainingTypeName(),
                entityAgain.getTrainingTypeEntity().getTrainingTypeName());
    }

    @Test
    void testNestedMappingWithTrainingType() {
        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result.getTrainingType());
        assertEquals("Yoga", result.getTrainingType().getTrainingTypeName());
    }

    @Test
    void testUsernameExtractionFromNestedEntities() {
        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertNotNull(result.getTraineeUsername());
        assertEquals("john.doe", result.getTraineeUsername());

        assertNotNull(result.getTrainerUsername());
        assertEquals("jane.smith", result.getTrainerUsername());
    }

    @Test
    void testTrainingDurationMinutesMapping() {
        trainingEntity.setTrainingDurationMinutes(120);

        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertEquals(120, result.getTrainingDurationMinutes());
    }

    @Test
    void testTrainingDateMapping() {
        LocalDate futureDate = LocalDate.of(2025, 6, 15);
        trainingEntity.setTrainingDate(futureDate);

        Training result = trainingMapper.toTrainingModel(trainingEntity);

        assertEquals(futureDate, result.getTrainingDate());
    }

    @Test
    void testMultipleTrainingsWithDifferentData() {
        TrainingEntity training1 = TrainingEntity.builder()
                .id(1L)
                .traineeEntity(traineeEntity)
                .trainerEntity(trainerEntity)
                .trainingName("Cardio Session")
                .trainingTypeEntity(trainingTypeEntity)
                .trainingDate(LocalDate.of(2024, 12, 1))
                .trainingDurationMinutes(45)
                .build();

        TrainingEntity training2 = TrainingEntity.builder()
                .id(2L)
                .traineeEntity(traineeEntity)
                .trainerEntity(trainerEntity)
                .trainingName("Strength Training")
                .trainingTypeEntity(trainingTypeEntity)
                .trainingDate(LocalDate.of(2024, 12, 2))
                .trainingDurationMinutes(60)
                .build();

        List<TrainingEntity> entities = Arrays.asList(training1, training2);

        List<Training> results = trainingMapper.toTrainingModels(entities);

        assertEquals(2, results.size());
        assertEquals("Cardio Session", results.get(0).getTrainingName());
        assertEquals(45, results.get(0).getTrainingDurationMinutes());
        assertEquals("Strength Training", results.get(1).getTrainingName());
        assertEquals(60, results.get(1).getTrainingDurationMinutes());
    }
}