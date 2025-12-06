package org.example.services.impl;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceDbImplTest {

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TraineeMapper traineeMapper;

    @InjectMocks
    private TraineeServiceDbImpl traineeService;

    private Trainee traineeModel;
    private TraineeEntity traineeEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        traineeEntity = TraineeEntity.builder()
                .id(1L)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .userEntity(userEntity)
                .build();

        traineeModel = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("password123".toCharArray())
                .isActive(true)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .build();
    }

    @Test
    void createTrainee_shouldCreateAndReturnTrainee() {
        when(traineeMapper.toTraineeEntity(traineeModel)).thenReturn(traineeEntity);
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Trainee result = traineeService.createTrainee(traineeModel);

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("John", result.getFirstName());
        verify(traineeMapper).toTraineeEntity(traineeModel);
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void getTraineeByUsername_shouldReturnTraineeWhenFound() {
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Optional<Trainee> result = traineeService.getTraineeByUsername("john.doe");

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());
        assertEquals("John", result.get().getFirstName());
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void getTraineeByUsername_shouldReturnEmptyWhenNotFound() {
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.getTraineeByUsername("unknown");

        assertFalse(result.isPresent());
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeMapper, never()).toTraineeModel(any());
    }

    @Test
    void updateTrainee_shouldUpdateAndReturnTrainee() {
        Trainee updatedModel = Trainee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1995, 8, 20))
                .address("456 Oak Ave")
                .build();

        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(updatedModel);

        Trainee result = traineeService.updateTrainee("john.doe", updatedModel);

        assertNotNull(result);
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper).updateEntity(updatedModel, traineeEntity);
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void updateTrainee_shouldThrowExceptionWhenTraineeNotFound() {
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                traineeService.updateTrainee("unknown", traineeModel)
        );
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeMapper, never()).updateEntity(any(), any());
        verify(traineeRepo, never()).save(any());
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteWhenTraineeExists() {
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));

        traineeService.deleteTraineeByUsername("john.doe");

        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).deleteByUsername("john.doe");
    }

    @Test
    void deleteTraineeByUsername_shouldNotDeleteWhenTraineeNotFound() {
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        traineeService.deleteTraineeByUsername("unknown");

        verify(traineeRepo).findByUsername("unknown");
        verify(traineeRepo, never()).deleteByUsername(anyString());
    }

    @Test
    void passwordMatches_shouldReturnTrueWhenPasswordMatches() {
        char[] password = "password123".toCharArray();
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));

        boolean result = traineeService.passwordMatches("john.doe", password);

        assertTrue(result);
        verify(traineeRepo).findByUsername("john.doe");
    }

    @Test
    void passwordMatches_shouldReturnFalseWhenPasswordDoesNotMatch() {
        char[] wrongPassword = "wrongpassword".toCharArray();
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));

        boolean result = traineeService.passwordMatches("john.doe", wrongPassword);

        assertFalse(result);
        verify(traineeRepo).findByUsername("john.doe");
    }

    @Test
    void passwordMatches_shouldReturnFalseWhenTraineeNotFound() {
        char[] password = "password123".toCharArray();
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = traineeService.passwordMatches("unknown", password);

        assertFalse(result);
        verify(traineeRepo).findByUsername("unknown");
    }

    @Test
    void changePassword_shouldUpdatePasswordAndReturnTrainee() {
        char[] newPassword = "newPassword456".toCharArray();
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Trainee result = traineeService.changePassword("john.doe", newPassword);

        assertNotNull(result);
        assertArrayEquals(newPassword, traineeEntity.getUserEntity().getPassword());
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void changePassword_shouldThrowExceptionWhenTraineeNotFound() {
        char[] newPassword = "newPassword456".toCharArray();
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                traineeService.changePassword("unknown", newPassword)
        );
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeRepo, never()).save(any());
    }

    @Test
    void setActiveStatus_shouldActivateTrainee() {
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Trainee result = traineeService.setActiveStatus("john.doe", true);

        assertNotNull(result);
        assertTrue(traineeEntity.getUserEntity().getIsActive());
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void setActiveStatus_shouldDeactivateTrainee() {
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Trainee result = traineeService.setActiveStatus("john.doe", false);

        assertNotNull(result);
        assertFalse(traineeEntity.getUserEntity().getIsActive());
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void setActiveStatus_shouldThrowExceptionWhenTraineeNotFound() {
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                traineeService.setActiveStatus("unknown", true)
        );
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeRepo, never()).save(any());
    }

    @Test
    void getAllTrainees_shouldReturnAllTrainees() {
        UserEntity user2 = UserEntity.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("pass456".toCharArray())
                .isActive(true)
                .build();

        TraineeEntity trainee2 = TraineeEntity.builder()
                .id(2L)
                .dateOfBirth(LocalDate.of(1995, 8, 20))
                .address("456 Oak Ave")
                .userEntity(user2)
                .build();

        Trainee traineeModel2 = Trainee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .username("jane.smith")
                .password("pass456".toCharArray())
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 8, 20))
                .address("456 Oak Ave")
                .build();

        List<TraineeEntity> entities = Arrays.asList(traineeEntity, trainee2);
        List<Trainee> models = Arrays.asList(traineeModel, traineeModel2);

        when(traineeRepo.findAll()).thenReturn(entities);
        when(traineeMapper.toTraineeModels(entities)).thenReturn(models);

        List<Trainee> result = traineeService.getAllTrainees();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        assertEquals("jane.smith", result.get(1).getUsername());
        verify(traineeRepo).findAll();
        verify(traineeMapper).toTraineeModels(entities);
    }

    @Test
    void getAllTrainees_shouldReturnEmptyListWhenNoTrainees() {
        when(traineeRepo.findAll()).thenReturn(Arrays.asList());
        when(traineeMapper.toTraineeModels(anyList())).thenReturn(Arrays.asList());

        List<Trainee> result = traineeService.getAllTrainees();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(traineeRepo).findAll();
        verify(traineeMapper).toTraineeModels(anyList());
    }
}
