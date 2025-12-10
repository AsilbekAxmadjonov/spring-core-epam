package org.example.services.impl;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.example.services.AuthenticationService;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceDbImplTest {

    @Mock
    private TraineeRepo traineeRepo;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private TraineeServiceDbImpl traineeService;

    private Trainee traineeModel;
    private TraineeEntity traineeEntity;
    private UserEntity userEntity;

    private final char[] dummyPassword = "dummyPass".toCharArray();

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

        verify(traineeMapper).toTraineeEntity(traineeModel);
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void getTraineeByUsername_shouldReturnTraineeWhenFound() {
        when(authenticationService.authenticate("john.doe", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Optional<Trainee> result = traineeService.getTraineeByUsername("john.doe", dummyPassword);

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());

        verify(authenticationService).authenticate("john.doe", dummyPassword);
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void getTraineeByUsername_shouldReturnEmptyWhenNotFound() {
        when(authenticationService.authenticate("unknown", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.getTraineeByUsername("unknown", dummyPassword);

        assertFalse(result.isPresent());

        verify(authenticationService).authenticate("unknown", dummyPassword);
        verify(traineeRepo).findByUsername("unknown");
    }

    @Test
    void updateTrainee_shouldUpdateAndReturnTrainee() {
        Trainee updatedModel = Trainee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1995, 8, 20))
                .address("456 Oak Ave")
                .build();

        when(authenticationService.authenticate("john.doe", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeRepo.save(traineeEntity)).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(updatedModel);

        Trainee result = traineeService.updateTrainee("john.doe", dummyPassword, updatedModel);

        assertNotNull(result);

        verify(authenticationService).authenticate("john.doe", dummyPassword);
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper).updateEntity(updatedModel, traineeEntity);
        verify(traineeRepo).save(traineeEntity);
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void updateTrainee_shouldThrowExceptionWhenNotFound() {
        when(authenticationService.authenticate("unknown", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainee("unknown", dummyPassword, traineeModel));

        verify(authenticationService).authenticate("unknown", dummyPassword);
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeMapper, never()).updateEntity(any(), any());
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteWhenExists() {
        when(authenticationService.authenticate("john.doe", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));

        traineeService.deleteTraineeByUsername("john.doe", dummyPassword);

        verify(authenticationService).authenticate("john.doe", dummyPassword);
        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).deleteByUsername("john.doe");
    }

    @Test
    void deleteTraineeByUsername_shouldNotDeleteWhenNotFound() {
        when(authenticationService.authenticate("unknown", dummyPassword)).thenReturn(traineeModel);
        when(traineeRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        traineeService.deleteTraineeByUsername("unknown", dummyPassword);

        verify(authenticationService).authenticate("unknown", dummyPassword);
        verify(traineeRepo).findByUsername("unknown");
        verify(traineeRepo, never()).deleteByUsername(anyString());
    }

    @Test
    void getAllTrainees_shouldReturnList() {
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

        assertEquals(2, result.size());
        verify(traineeRepo).findAll();
        verify(traineeMapper).toTraineeModels(entities);
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList() {
        when(traineeRepo.findAll()).thenReturn(List.of());
        when(traineeMapper.toTraineeModels(anyList())).thenReturn(List.of());

        List<Trainee> result = traineeService.getAllTrainees();

        assertTrue(result.isEmpty());
        verify(traineeRepo).findAll();
        verify(traineeMapper).toTraineeModels(anyList());
    }
}