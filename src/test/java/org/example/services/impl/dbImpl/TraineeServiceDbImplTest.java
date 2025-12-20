package org.example.services.impl.dbImpl;

import org.example.persistance.entity.TraineeEntity;
import org.example.persistance.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.persistance.model.Trainee;
import org.example.persistance.repository.TraineeRepo;
import org.example.persistance.repository.UserRepo;
import org.example.services.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private TraineeServiceDbImpl traineeService;

    private Trainee traineeModel;
    private TraineeEntity traineeEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        traineeModel = Trainee.builder()
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .build();

        userEntity = UserEntity.builder()
                .id(1L)
                .username("John.Doe")
                .firstName("John")
                .lastName("Doe")
                .password("encoded".toCharArray())
                .isActive(true)
                .build();

        traineeEntity = TraineeEntity.builder()
                .id(1L)
                .userEntity(userEntity)
                .dateOfBirth(LocalDate.of(1990, 5, 15))
                .address("123 Main St")
                .build();
    }

    @Test
    void createTrainee_shouldCreateAndReturnTrainee() {
        when(userRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(userRepo.save(any(UserEntity.class)))
                .thenReturn(userEntity);

        when(traineeRepo.save(any(TraineeEntity.class)))
                .thenReturn(traineeEntity);

        when(tokenService.generateToken(anyString()))
                .thenReturn("jwt-token");

        when(traineeMapper.toTraineeModel(any()))
                .thenReturn(traineeModel);

        Trainee result = traineeService.createTrainee(traineeModel);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertNotNull(result.getToken());

        verify(tokenService).generateToken(anyString());
        verify(userRepo).save(any(UserEntity.class));
        verify(traineeRepo).save(any(TraineeEntity.class));
    }

    @Test
    void updateTrainee_shouldUpdateAndReturnTrainee() {
        when(traineeRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(traineeEntity));

        when(traineeRepo.save(traineeEntity))
                .thenReturn(traineeEntity);

        when(traineeMapper.toTraineeModel(traineeEntity))
                .thenReturn(traineeModel);

        Trainee result =
                traineeService.updateTrainee("john.doe", traineeModel);

        assertNotNull(result);

        verify(traineeMapper)
                .updateEntity(traineeModel, traineeEntity);
        verify(traineeRepo)
                .save(traineeEntity);
    }

    @Test
    void updateTrainee_shouldThrowExceptionWhenNotFound() {
        when(traineeRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainee("john.doe", traineeModel));
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteWhenExists() {
        when(traineeRepo.findByUsername("john.doe"))
                .thenReturn(Optional.of(traineeEntity));

        traineeService.deleteTraineeByUsername("john.doe");

        verify(traineeRepo).deleteByUsername("john.doe");
    }

    @Test
    void deleteTraineeByUsername_shouldDoNothingWhenNotFound() {
        when(traineeRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        traineeService.deleteTraineeByUsername("john.doe");

        verify(traineeRepo, never())
                .deleteByUsername(anyString());
    }

    @Test
    void getAllTrainees_shouldReturnList() {
        when(traineeRepo.findAll())
                .thenReturn(List.of(traineeEntity));

        when(traineeMapper.toTraineeModels(anyList()))
                .thenReturn(List.of(traineeModel));

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(1, result.size());
    }

    @Test
    void getAllTrainees_shouldReturnEmptyList() {
        when(traineeRepo.findAll())
                .thenReturn(List.of());

        when(traineeMapper.toTraineeModels(anyList()))
                .thenReturn(List.of());

        List<Trainee> result = traineeService.getAllTrainees();

        assertTrue(result.isEmpty());
    }
}