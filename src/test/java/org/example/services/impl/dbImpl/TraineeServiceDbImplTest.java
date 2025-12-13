package org.example.services.impl.dbImpl;

import org.example.entity.TraineeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TraineeMapper;
import org.example.model.Trainee;
import org.example.repository.TraineeRepo;
import org.example.repository.UserRepo;
import org.example.security.AuthenticationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
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
    private UserRepo userRepo;

    @InjectMocks
    private TraineeServiceDbImpl traineeService;

    private MockedStatic<AuthenticationContext> authContextMock;

    private Trainee traineeModel;
    private TraineeEntity traineeEntity;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        authContextMock = mockStatic(AuthenticationContext.class);

        userEntity = UserEntity.builder()
                .id(1L)
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

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }

    @Test
    void createTrainee_shouldCreateAndReturnTrainee() {
        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.of(userEntity));
        when(traineeRepo.save(any(TraineeEntity.class))).thenReturn(traineeEntity);
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Trainee result = traineeService.createTrainee(traineeModel);

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());

        verify(userRepo).findByUsername("john.doe");
        verify(traineeRepo).save(any(TraineeEntity.class));
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void createTrainee_shouldThrowExceptionWhenUserNotFound() {
        when(userRepo.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.createTrainee(traineeModel));

        verify(userRepo).findByUsername("john.doe");
        verify(traineeRepo, never()).save(any());
    }

    @Test
    void getTraineeByUsername_shouldReturnTraineeWhenFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));
        when(traineeMapper.toTraineeModel(traineeEntity)).thenReturn(traineeModel);

        Optional<Trainee> result = traineeService.getTraineeByUsername("john.doe");

        assertTrue(result.isPresent());
        assertEquals("john.doe", result.get().getUsername());

        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper).toTraineeModel(traineeEntity);
    }

    @Test
    void getTraineeByUsername_shouldReturnEmptyWhenNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.empty());

        Optional<Trainee> result = traineeService.getTraineeByUsername("john.doe");

        assertFalse(result.isPresent());
        verify(traineeRepo).findByUsername("john.doe");
    }

    @Test
    void getTraineeByUsername_shouldThrowSecurityExceptionWhenNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.getTraineeByUsername("john.doe"));

        assertEquals("Trainee not authenticated", ex.getMessage());
        verify(traineeRepo, never()).findByUsername(anyString());
    }

    @Test
    void getTraineeByUsername_shouldThrowSecurityExceptionWhenDifferentUser() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.getTraineeByUsername("john.doe"));

        assertEquals("Trainee not authenticated", ex.getMessage());
        verify(traineeRepo, never()).findByUsername(anyString());
    }

    @Test
    void updateTrainee_shouldUpdateAndReturnTrainee() {
        Trainee updatedModel = Trainee.builder()
                .firstName("Jane")
                .lastName("Smith")
                .dateOfBirth(LocalDate.of(1995, 8, 20))
                .address("456 Oak Ave")
                .build();

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
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
    void updateTrainee_shouldThrowSecurityExceptionWhenNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.updateTrainee("john.doe", traineeModel));

        assertEquals("User not authenticated", ex.getMessage());
        verify(traineeRepo, never()).findByUsername(anyString());
    }

    @Test
    void updateTrainee_shouldThrowExceptionWhenNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> traineeService.updateTrainee("john.doe", traineeModel));

        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeMapper, never()).updateEntity(any(), any());
    }

    @Test
    void deleteTraineeByUsername_shouldDeleteWhenExists() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.of(traineeEntity));

        traineeService.deleteTraineeByUsername("john.doe");

        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo).deleteByUsername("john.doe");
    }

    @Test
    void deleteTraineeByUsername_shouldNotDeleteWhenNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("john.doe");
        when(traineeRepo.findByUsername("john.doe")).thenReturn(Optional.empty());

        traineeService.deleteTraineeByUsername("john.doe");

        verify(traineeRepo).findByUsername("john.doe");
        verify(traineeRepo, never()).deleteByUsername(anyString());
    }

    @Test
    void deleteTraineeByUsername_shouldThrowSecurityExceptionWhenNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> traineeService.deleteTraineeByUsername("john.doe"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(traineeRepo, never()).findByUsername(anyString());
    }

    @Test
    void getAllTrainees_shouldReturnList() {
        UserEntity user2 = UserEntity.builder()
                .id(2L)
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