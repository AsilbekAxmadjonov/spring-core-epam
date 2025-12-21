package org.example.mapper;

import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainingTypeMapperTest {

    private TrainingTypeMapper trainingTypeMapper;

    @BeforeEach
    void setUp() {
        trainingTypeMapper = Mappers.getMapper(TrainingTypeMapper.class);
    }

    @Test
    void testToModel_Success() {
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        TrainingType result = trainingTypeMapper.toModel(entity);

        assertNotNull(result);
        assertEquals("Yoga", result.getTrainingTypeName());
    }

    @Test
    void testToModel_WithNullEntity() {
        TrainingType result = trainingTypeMapper.toModel(null);

        assertNull(result);
    }

    @Test
    void testToModel_WithDifferentTrainingTypes() {
        TrainingTypeEntity cardioEntity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Cardio")
                .build();

        TrainingTypeEntity strengthEntity = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Strength Training")
                .build();

        TrainingType cardio = trainingTypeMapper.toModel(cardioEntity);
        TrainingType strength = trainingTypeMapper.toModel(strengthEntity);

        assertNotNull(cardio);
        assertEquals("Cardio", cardio.getTrainingTypeName());

        assertNotNull(strength);
        assertEquals("Strength Training", strength.getTrainingTypeName());
    }

    @Test
    void testToEntity_Success() {
        TrainingType model = new TrainingType("Pilates");

        TrainingTypeEntity result = trainingTypeMapper.toEntity(model);

        assertNotNull(result);
        assertEquals("Pilates", result.getTrainingTypeName());
        assertNull(result.getId());
    }

    @Test
    void testToEntity_WithNullModel() {
        TrainingTypeEntity result = trainingTypeMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void testToEntity_WithDifferentTrainingTypes() {
        TrainingType zumba = new TrainingType("Zumba");
        TrainingType boxing = new TrainingType("Boxing");

        TrainingTypeEntity zumbaEntity = trainingTypeMapper.toEntity(zumba);
        TrainingTypeEntity boxingEntity = trainingTypeMapper.toEntity(boxing);

        assertNotNull(zumbaEntity);
        assertEquals("Zumba", zumbaEntity.getTrainingTypeName());

        assertNotNull(boxingEntity);
        assertEquals("Boxing", boxingEntity.getTrainingTypeName());
    }

    @Test
    void testToModels_Success() {
        TrainingTypeEntity entity1 = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("CrossFit")
                .build();

        TrainingTypeEntity entity2 = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Swimming")
                .build();

        TrainingTypeEntity entity3 = TrainingTypeEntity.builder()
                .id(3L)
                .trainingTypeName("Running")
                .build();

        List<TrainingTypeEntity> entities = Arrays.asList(entity1, entity2, entity3);

        List<TrainingType> result = trainingTypeMapper.toModels(entities);

        assertNotNull(result);
        assertEquals(3, result.size());

        assertEquals("CrossFit", result.get(0).getTrainingTypeName());
        assertEquals("Swimming", result.get(1).getTrainingTypeName());
        assertEquals("Running", result.get(2).getTrainingTypeName());
    }

    @Test
    void testToModels_WithEmptyList() {
        List<TrainingTypeEntity> emptyList = Collections.emptyList();

        List<TrainingType> result = trainingTypeMapper.toModels(emptyList);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testToModels_WithNullList() {
        List<TrainingType> result = trainingTypeMapper.toModels(null);

        assertNull(result);
    }

    @Test
    void testToModels_WithSingleElement() {
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Martial Arts")
                .build();

        List<TrainingTypeEntity> entities = Collections.singletonList(entity);

        List<TrainingType> result = trainingTypeMapper.toModels(entities);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Martial Arts", result.get(0).getTrainingTypeName());
    }

    @Test
    void testRoundTripMapping() {
        TrainingTypeEntity originalEntity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Dance")
                .build();

        TrainingType model = trainingTypeMapper.toModel(originalEntity);
        TrainingTypeEntity entityAgain = trainingTypeMapper.toEntity(model);

        assertEquals(originalEntity.getTrainingTypeName(), entityAgain.getTrainingTypeName());
    }

    @Test
    void testModelToEntityToModel() {
        TrainingType originalModel = new TrainingType("Aerobics");

        TrainingTypeEntity entity = trainingTypeMapper.toEntity(originalModel);
        TrainingType modelAgain = trainingTypeMapper.toModel(entity);

        assertEquals(originalModel.getTrainingTypeName(), modelAgain.getTrainingTypeName());
    }

    @Test
    void testTrainingTypeNamePreserved() {
        String trainingTypeName = "High-Intensity Interval Training";
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(10L)
                .trainingTypeName(trainingTypeName)
                .build();

        TrainingType model = trainingTypeMapper.toModel(entity);

        assertEquals(trainingTypeName, model.getTrainingTypeName());
    }

    @Test
    void testEntityIgnoresTrainingEntitiesList() {
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Cycling")
                .trainingEntities(null)
                .build();

        TrainingType model = trainingTypeMapper.toModel(entity);

        assertNotNull(model);
        assertEquals("Cycling", model.getTrainingTypeName());
    }

    @Test
    void testMappingWithSpecialCharacters() {
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Yoga & Meditation")
                .build();

        TrainingType model = trainingTypeMapper.toModel(entity);
        TrainingTypeEntity entityAgain = trainingTypeMapper.toEntity(model);

        assertEquals("Yoga & Meditation", model.getTrainingTypeName());
        assertEquals("Yoga & Meditation", entityAgain.getTrainingTypeName());
    }

    @Test
    void testMappingWithLongName() {
        String longName = "Advanced Cardiovascular Training with Resistance Band Exercises";
        TrainingTypeEntity entity = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName(longName)
                .build();

        TrainingType model = trainingTypeMapper.toModel(entity);

        assertEquals(longName, model.getTrainingTypeName());
    }

    @Test
    void testMultipleMappingsInSequence() {
        List<String> trainingTypeNames = Arrays.asList(
                "Yoga", "Pilates", "CrossFit", "Swimming", "Running"
        );

        for (String typeName : trainingTypeNames) {
            TrainingTypeEntity entity = TrainingTypeEntity.builder()
                    .id(1L)
                    .trainingTypeName(typeName)
                    .build();

            TrainingType model = trainingTypeMapper.toModel(entity);

            assertNotNull(model);
            assertEquals(typeName, model.getTrainingTypeName());
        }
    }

    @Test
    void testEqualityBasedOnTrainingTypeName() {
        TrainingType model1 = new TrainingType("Yoga");
        TrainingType model2 = new TrainingType("Yoga");
        TrainingType model3 = new TrainingType("Pilates");

        assertEquals(model1, model2);
        assertNotEquals(model1, model3);
    }
}
