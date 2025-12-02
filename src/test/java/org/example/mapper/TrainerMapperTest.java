package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrainerMapperTest {

    private TrainerMapper trainerMapper;

    @BeforeEach
    void setUp() {
        trainerMapper = Mappers.getMapper(TrainerMapper.class);
    }

    @Test
    void toTrainerModel_shouldMapEntityToModel() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("John")
                .lastName("Smith")
                .username("john.smith")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TrainingTypeEntity trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        TrainerEntity trainerEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(trainingType)
                .userEntity(userEntity)
                .build();

        Trainer trainer = trainerMapper.toTrainerModel(trainerEntity);

        assertNotNull(trainer);
        assertEquals("John", trainer.getFirstName());
        assertEquals("Smith", trainer.getLastName());
        assertEquals("john.smith", trainer.getUsername());
        assertArrayEquals("password123".toCharArray(), trainer.getPassword());
        assertTrue(trainer.isActive());
        assertEquals("Fitness", trainer.getSpecialization());
    }

    @Test
    void toTrainerModel_shouldHandleNullUserEntity() {
        TrainingTypeEntity trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();

        TrainerEntity trainerEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(trainingType)
                .userEntity(null)
                .build();

        Trainer trainer = trainerMapper.toTrainerModel(trainerEntity);

        assertNotNull(trainer);
        assertNull(trainer.getFirstName());
        assertNull(trainer.getLastName());
        assertNull(trainer.getUsername());
        assertEquals("Yoga", trainer.getSpecialization());
    }

    @Test
    void toTrainerModel_shouldHandleNullSpecialization() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("Jane")
                .lastName("Doe")
                .username("jane.doe")
                .password("pass456".toCharArray())
                .isActive(true)
                .build();

        TrainerEntity trainerEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(null)
                .userEntity(userEntity)
                .build();

        Trainer trainer = trainerMapper.toTrainerModel(trainerEntity);

        assertNotNull(trainer);
        assertEquals("Jane", trainer.getFirstName());
        assertEquals("Doe", trainer.getLastName());
        assertNull(trainer.getSpecialization());
    }

    @Test
    void toTrainerModel_shouldHandleNullEntity() {
        Trainer trainer = trainerMapper.toTrainerModel(null);

        assertNull(trainer);
    }

    @Test
    void stringToTrainingType_shouldConvertStringToEntity() {
        TrainingTypeEntity result = trainerMapper.stringToTrainingType("Cardio");

        assertNotNull(result);
        assertEquals("Cardio", result.getTrainingTypeName());
    }

    @Test
    void stringToTrainingType_shouldHandleNull() {
        TrainingTypeEntity result = trainerMapper.stringToTrainingType(null);

        assertNull(result);
    }

    @Test
    void toTrainerModels_shouldMapListOfEntitiesToModels() {
        UserEntity user1 = UserEntity.builder()
                .firstName("Alice")
                .lastName("Cooper")
                .username("alice.c")
                .password("pass1".toCharArray())
                .isActive(true)
                .build();

        UserEntity user2 = UserEntity.builder()
                .firstName("Bob")
                .lastName("Martin")
                .username("bob.m")
                .password("pass2".toCharArray())
                .isActive(false)
                .build();

        TrainingTypeEntity type1 = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Pilates")
                .build();

        TrainingTypeEntity type2 = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("CrossFit")
                .build();

        TrainerEntity trainer1 = TrainerEntity.builder()
                .id(1L)
                .specialization(type1)
                .userEntity(user1)
                .build();

        TrainerEntity trainer2 = TrainerEntity.builder()
                .id(2L)
                .specialization(type2)
                .userEntity(user2)
                .build();

        List<TrainerEntity> entities = Arrays.asList(trainer1, trainer2);

        List<Trainer> trainers = trainerMapper.toTrainerModels(entities);

        assertNotNull(trainers);
        assertEquals(2, trainers.size());

        assertEquals("Alice", trainers.get(0).getFirstName());
        assertEquals("Cooper", trainers.get(0).getLastName());
        assertEquals("alice.c", trainers.get(0).getUsername());
        assertTrue(trainers.get(0).isActive());
        assertEquals("Pilates", trainers.get(0).getSpecialization());

        assertEquals("Bob", trainers.get(1).getFirstName());
        assertEquals("Martin", trainers.get(1).getLastName());
        assertEquals("bob.m", trainers.get(1).getUsername());
        assertFalse(trainers.get(1).isActive());
        assertEquals("CrossFit", trainers.get(1).getSpecialization());
    }

    @Test
    void toTrainerModels_shouldHandleEmptyList() {
        List<TrainerEntity> entities = Arrays.asList();

        List<Trainer> trainers = trainerMapper.toTrainerModels(entities);

        assertNotNull(trainers);
        assertTrue(trainers.isEmpty());
    }

    @Test
    void toTrainerModels_shouldHandleNullList() {
        List<Trainer> trainers = trainerMapper.toTrainerModels(null);

        assertNull(trainers);
    }

    @Test
    void updateEntity_shouldUpdateExistingEntity() {
        UserEntity existingUser = UserEntity.builder()
                .firstName("Old")
                .lastName("Name")
                .username("old.user")
                .password("oldpass".toCharArray())
                .isActive(false)
                .build();

        TrainingTypeEntity existingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("OldType")
                .build();

        TrainerEntity existingEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(existingType)
                .userEntity(existingUser)
                .build();

        Trainer updatedModel = Trainer.builder()
                .firstName("New")
                .lastName("Name")
                .username("new.user")
                .password("newpass".toCharArray())
                .isActive(true)
                .specialization("NewType")
                .build();

        trainerMapper.updateEntity(updatedModel, existingEntity);

        assertEquals(1L, existingEntity.getId());
        assertNotNull(existingEntity.getUserEntity());
        assertEquals(existingUser, existingEntity.getUserEntity());
        assertNotNull(existingEntity.getSpecialization());
        assertEquals(existingType, existingEntity.getSpecialization());
    }

    @Test
    void updateEntity_shouldHandleNullSpecialization() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .build();

        TrainingTypeEntity trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        TrainerEntity entity = TrainerEntity.builder()
                .id(1L)
                .specialization(trainingType)
                .userEntity(userEntity)
                .build();

        Trainer model = Trainer.builder()
                .specialization(null)
                .build();

        trainerMapper.updateEntity(model, entity);

        assertNotNull(entity.getSpecialization());
        assertEquals(trainingType, entity.getSpecialization());
    }
}