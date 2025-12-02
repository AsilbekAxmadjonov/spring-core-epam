package org.example.mapper;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TraineeMapperTest {

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = Mappers.getMapper(TraineeMapper.class);
    }

    @Test
    void toTraineeModel_shouldMapEntityToModel() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St, New York")
                .userEntity(userEntity)
                .build();

        Trainee trainee = traineeMapper.toTraineeModel(traineeEntity);

        assertNotNull(trainee);
        assertEquals("John", trainee.getFirstName());
        assertEquals("Doe", trainee.getLastName());
        assertEquals("john.doe", trainee.getUsername());
        assertArrayEquals("password123".toCharArray(), trainee.getPassword());
        assertTrue(trainee.isActive());
        assertEquals(LocalDate.of(1990, 5, 15), trainee.getDateOfBirth());
        assertEquals("123 Main St, New York", trainee.getAddress());
    }

    @Test
    void toTraineeModel_shouldHandleNullUserEntity() {
        TraineeEntity traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .userEntity(null)
                .build();

        Trainee trainee = traineeMapper.toTraineeModel(traineeEntity);

        assertNotNull(trainee);
        assertNull(trainee.getFirstName());
        assertNull(trainee.getLastName());
        assertNull(trainee.getUsername());
        assertEquals(LocalDate.of(1990, 5, 15), trainee.getDateOfBirth());
        assertEquals("123 Main St", trainee.getAddress());
    }

    @Test
    void toTraineeModel_shouldHandleNullEntity() {
        Trainee trainee = traineeMapper.toTraineeModel(null);
        assertNull(trainee);
    }

    @Test
    void toTraineeEntity_shouldHandleNullModel() {
        TraineeEntity traineeEntity = traineeMapper.toTraineeEntity(null);
        assertNull(traineeEntity);
    }

    @Test
    void toTraineeModels_shouldMapListOfEntitiesToModels() {
        UserEntity user1 = UserEntity.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .username("alice.j")
                .password("pass1".toCharArray())
                .isActive(true)
                .build();

        UserEntity user2 = UserEntity.builder()
                .firstName("Bob")
                .lastName("Williams")
                .username("bob.w")
                .password("pass2".toCharArray())
                .isActive(false)
                .build();

        TraineeEntity trainee1 = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1992, 3, 10))
                .address("100 First St")
                .userEntity(user1)
                .build();

        TraineeEntity trainee2 = TraineeEntity.builder()
                .id(2L)
                .dateOfBirth(LocalDate.of(1988, 7, 25))
                .address("200 Second St")
                .userEntity(user2)
                .build();

        List<TraineeEntity> entities = Arrays.asList(trainee1, trainee2);

        List<Trainee> trainees = traineeMapper.toTraineeModels(entities);

        assertNotNull(trainees);
        assertEquals(2, trainees.size());

        assertEquals("Alice", trainees.get(0).getFirstName());
        assertEquals("Johnson", trainees.get(0).getLastName());
        assertEquals("alice.j", trainees.get(0).getUsername());
        assertTrue(trainees.get(0).isActive());

        assertEquals("Bob", trainees.get(1).getFirstName());
        assertEquals("Williams", trainees.get(1).getLastName());
        assertEquals("bob.w", trainees.get(1).getUsername());
        assertFalse(trainees.get(1).isActive());
    }

    @Test
    void toTraineeModels_shouldHandleEmptyList() {
        List<TraineeEntity> entities = Arrays.asList();

        List<Trainee> trainees = traineeMapper.toTraineeModels(entities);

        assertNotNull(trainees);
        assertTrue(trainees.isEmpty());
    }

    @Test
    void toTraineeModels_shouldHandleNullList() {
        List<Trainee> trainees = traineeMapper.toTraineeModels(null);
        assertNull(trainees);
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

        TraineeEntity existingEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1985, 1, 1))
                .address("Old Address")
                .userEntity(existingUser)
                .build();

        Trainee updatedModel = Trainee.builder()
                .firstName("New")
                .lastName("Name")
                .username("new.user")
                .password("newpass".toCharArray())
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 12, 31))
                .address("New Address")
                .build();

        traineeMapper.updateEntity(updatedModel, existingEntity);

        assertEquals(1L, existingEntity.getId());
        assertNotNull(existingEntity.getUserEntity());
        assertEquals(existingUser, existingEntity.getUserEntity());
        assertEquals(LocalDate.of(1990, 12, 31), existingEntity.getDateOfBirth());
        assertEquals("New Address", existingEntity.getAddress());
    }

    @Test
    void updateEntity_shouldHandleNullDateOfBirth() {
        UserEntity userEntity = UserEntity.builder()
                .firstName("Test")
                .lastName("User")
                .build();

        TraineeEntity entity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .address("Test Address")
                .userEntity(userEntity)
                .build();

        Trainee model = Trainee.builder()
                .dateOfBirth(null)
                .address("Updated Address")
                .build();

        traineeMapper.updateEntity(model, entity);

        assertNull(entity.getDateOfBirth());
        assertEquals("Updated Address", entity.getAddress());
    }
}
