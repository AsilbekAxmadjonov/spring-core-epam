package org.example.services.impl.dbImpl;

import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingTypeRepo;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceDbImplTest {

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private TrainerServiceDbImpl trainerService;

    private MockedStatic<AuthenticationContext> authContextMock;

    private Trainer trainerModel;
    private TrainerEntity trainerEntity;
    private UserEntity userEntity;
    private TrainingTypeEntity trainingType;

    @BeforeEach
    void setUp() {
        authContextMock = mockStatic(AuthenticationContext.class);

        userEntity = UserEntity.builder()
                .id(1L)
                .firstName("Mike")
                .lastName("Johnson")
                .username("mike.johnson")
                .password("password123".toCharArray())
                .isActive(true)
                .build();

        trainingType = TrainingTypeEntity.builder()
                .id(1L)
                .trainingTypeName("Fitness")
                .build();

        trainerEntity = TrainerEntity.builder()
                .id(1L)
                .specialization(trainingType)
                .userEntity(userEntity)
                .build();

        trainerModel = Trainer.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .username("mike.johnson")
                .password("password123".toCharArray())
                .isActive(true)
                .specialization("Fitness")
                .build();
    }

    @AfterEach
    void tearDown() {
        authContextMock.close();
    }

    @Test
    void createTrainer_shouldCreateAndReturnTrainer() {
        when(userRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(userEntity));
        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.of(trainingType));
        when(trainerRepo.save(any(TrainerEntity.class))).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Trainer result = trainerService.createTrainer(trainerModel);

        assertNotNull(result);
        assertEquals("mike.johnson", result.getUsername());
        assertEquals("Mike", result.getFirstName());
        assertEquals("Fitness", result.getSpecialization());

        verify(userRepo).findByUsername("mike.johnson");
        verify(trainingTypeRepo).findByTrainingTypeName("Fitness");
        verify(trainerRepo).save(any(TrainerEntity.class));
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void createTrainer_shouldThrowExceptionWhenUserNotFound() {
        when(userRepo.findByUsername("mike.johnson")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.createTrainer(trainerModel));

        verify(userRepo).findByUsername("mike.johnson");
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void createTrainer_shouldThrowExceptionWhenTrainingTypeNotFound() {
        when(userRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(userEntity));
        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(trainerModel));

        verify(userRepo).findByUsername("mike.johnson");
        verify(trainingTypeRepo).findByTrainingTypeName("Fitness");
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainerWhenFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("mike.johnson");
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Optional<Trainer> result = trainerService.getTrainerByUsername("mike.johnson");

        assertTrue(result.isPresent());
        assertEquals("mike.johnson", result.get().getUsername());

        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void getTrainerByUsername_shouldReturnEmptyWhenNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("mike.johnson");
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainerByUsername("mike.johnson");

        assertFalse(result.isPresent());
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerMapper, never()).toTrainerModel(any());
    }

    @Test
    void getTrainerByUsername_shouldThrowSecurityExceptionWhenNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.getTrainerByUsername("mike.johnson"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerRepo, never()).findByUsername(anyString());
    }

    @Test
    void getTrainerByUsername_shouldThrowSecurityExceptionWhenDifferentUser() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("other.user");

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.getTrainerByUsername("mike.johnson"));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerRepo, never()).findByUsername(anyString());
    }

    @Test
    void updateTrainer_shouldUpdateAndReturnTrainer() {
        Trainer updatedModel = Trainer.builder()
                .firstName("Michael")
                .lastName("Smith")
                .specialization("Yoga")
                .build();

        TrainingTypeEntity yogaType = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Yoga")
                .build();

        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("mike.johnson");
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainingTypeRepo.findByTrainingTypeName("Yoga")).thenReturn(Optional.of(yogaType));
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(updatedModel);

        Trainer result = trainerService.updateTrainer("mike.johnson", updatedModel);

        assertNotNull(result);

        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainingTypeRepo).findByTrainingTypeName("Yoga");
        verify(trainerMapper).updateEntity(updatedModel, trainerEntity);
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void updateTrainer_shouldThrowSecurityExceptionWhenNotAuthenticated() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

        SecurityException ex = assertThrows(SecurityException.class,
                () -> trainerService.updateTrainer("mike.johnson", trainerModel));

        assertEquals("User not authenticated", ex.getMessage());
        verify(trainerRepo, never()).findByUsername(anyString());
    }

    @Test
    void updateTrainer_shouldThrowExceptionWhenTrainerNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("mike.johnson");
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.updateTrainer("mike.johnson", trainerModel));

        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerMapper, never()).updateEntity(any(), any());
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void updateTrainer_shouldThrowExceptionWhenTrainingTypeNotFound() {
        authContextMock.when(AuthenticationContext::getAuthenticatedUser).thenReturn("mike.johnson");
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.updateTrainer("mike.johnson", trainerModel));

        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainingTypeRepo).findByTrainingTypeName("Fitness");
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void getAllTrainers_shouldReturnAllTrainers() {
        UserEntity user2 = UserEntity.builder()
                .id(2L)
                .firstName("Sarah")
                .lastName("Connor")
                .username("sarah.connor")
                .password("pass456".toCharArray())
                .isActive(true)
                .build();

        TrainingTypeEntity type2 = TrainingTypeEntity.builder()
                .id(2L)
                .trainingTypeName("Yoga")
                .build();

        TrainerEntity trainer2 = TrainerEntity.builder()
                .id(2L)
                .specialization(type2)
                .userEntity(user2)
                .build();

        Trainer trainerModel2 = Trainer.builder()
                .firstName("Sarah")
                .lastName("Connor")
                .username("sarah.connor")
                .password("pass456".toCharArray())
                .isActive(true)
                .specialization("Yoga")
                .build();

        List<TrainerEntity> entities = Arrays.asList(trainerEntity, trainer2);
        List<Trainer> models = Arrays.asList(trainerModel, trainerModel2);

        when(trainerRepo.findAll()).thenReturn(entities);
        when(trainerMapper.toTrainerModels(entities)).thenReturn(models);

        List<Trainer> result = trainerService.getAllTrainers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("mike.johnson", result.get(0).getUsername());
        assertEquals("Fitness", result.get(0).getSpecialization());
        assertEquals("sarah.connor", result.get(1).getUsername());
        assertEquals("Yoga", result.get(1).getSpecialization());

        verify(trainerRepo).findAll();
        verify(trainerMapper).toTrainerModels(entities);
    }

    @Test
    void getAllTrainers_shouldReturnEmptyListWhenNoTrainers() {
        when(trainerRepo.findAll()).thenReturn(Arrays.asList());
        when(trainerMapper.toTrainerModels(anyList())).thenReturn(Arrays.asList());

        List<Trainer> result = trainerService.getAllTrainers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(trainerRepo).findAll();
        verify(trainerMapper).toTrainerModels(anyList());
    }
}