package org.example.services.impl.dbImpl;

import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.persistance.entity.TrainerEntity;
import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.entity.UserEntity;
import org.example.persistance.model.Trainer;
import org.example.persistance.model.TrainerRegistrationResult;
import org.example.persistance.repository.TrainerRepo;
import org.example.persistance.repository.TrainingTypeRepo;
import org.example.persistance.repository.UserRepo;
import org.example.services.TokenService;
import org.example.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceDbImplTest {

    @Mock private TrainerRepo trainerRepo;
    @Mock private TrainerMapper trainerMapper;
    @Mock private TrainingTypeRepo trainingTypeRepo;
    @Mock private UserRepo userRepo;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private TokenService tokenService;
    @Mock private UserService userService; // required by constructor

    @InjectMocks
    private TrainerServiceDbImpl trainerService;

    private Trainer trainerModel;
    private TrainerEntity trainerEntity;
    private UserEntity userEntity;
    private TrainingTypeEntity trainingType;

    @BeforeEach
    void setUp() {
        trainerModel = Trainer.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .specialization("Fitness")
                .build();

        userEntity = UserEntity.builder()
                .username("Mike.Johnson")
                .firstName("Mike")
                .lastName("Johnson")
                .isActive(true)
                .build();

        trainingType = TrainingTypeEntity.builder()
                .trainingTypeName("Fitness")
                .build();

        trainerEntity = TrainerEntity.builder()
                .userEntity(userEntity)
                .specialization(trainingType)
                .build();
    }

    @Test
    void createTrainer_success() {
        // username availability check: first call returns empty => username is free
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.of(trainingType));
        when(trainerRepo.save(any(TrainerEntity.class))).thenReturn(trainerEntity);

        when(tokenService.generateToken(anyString())).thenReturn("jwt-token");

        TrainerRegistrationResult result = trainerService.createTrainer(trainerModel);

        assertNotNull(result);
        assertNotNull(result.getUsername());
        assertNotNull(result.getTemporaryPassword());
        assertNotNull(result.getToken());

        // password is random; just validate expectations
        assertEquals(10, result.getTemporaryPassword().length());
        assertEquals("jwt-token", result.getToken());

        verify(userRepo).save(any(UserEntity.class));
        verify(trainerRepo).save(any(TrainerEntity.class));
        verify(tokenService).generateToken(anyString());

        // verify user password was encoded and stored as char[]
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepo).save(userCaptor.capture());
        UserEntity savedUser = userCaptor.getValue();
        assertNotNull(savedUser.getPassword());
        assertArrayEquals("encoded-password".toCharArray(), savedUser.getPassword());
        assertTrue(Boolean.TRUE.equals(savedUser.getIsActive()));
    }

    @Test
    void createTrainer_shouldAppendCounter_whenUsernameAlreadyExists() {
        // base username "Mike.Johnson" is taken, then "Mike.Johnson1" is free
        when(userRepo.findByUsername("Mike.Johnson")).thenReturn(Optional.of(userEntity));
        when(userRepo.findByUsername("Mike.Johnson1")).thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");
        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.of(trainingType));
        when(trainerRepo.save(any(TrainerEntity.class))).thenReturn(trainerEntity);
        when(tokenService.generateToken(anyString())).thenReturn("jwt-token");

        TrainerRegistrationResult result = trainerService.createTrainer(trainerModel);

        assertEquals("Mike.Johnson1", result.getUsername());
    }

    @Test
    void createTrainer_trainingTypeNotFound() {
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        // ✅ needed to avoid NPE on toCharArray()
        when(passwordEncoder.encode(anyString())).thenReturn("encoded-password");

        when(userRepo.save(any(UserEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        // ✅ trigger your expected exception
        when(trainingTypeRepo.findByTrainingTypeName("Fitness")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(trainerModel));

        verify(trainerRepo, never()).save(any());
    }



    @Test
    void getTrainerByUsername_found() {
        when(trainerRepo.findByUsername("Mike.Johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Optional<Trainer> result = trainerService.getTrainerByUsername("Mike.Johnson");

        assertTrue(result.isPresent());
        assertEquals("Mike", result.get().getFirstName());
        verify(trainerRepo).findByUsername("Mike.Johnson");
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void getTrainerByUsername_notFound() {
        when(trainerRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainerByUsername("Mike.Johnson");

        assertTrue(result.isEmpty());
        verify(trainerMapper, never()).toTrainerModel(any());
    }

    @Test
    void updateTrainer_success_nameNotChanged_shouldNotRegenerateUsername() {
        when(trainerRepo.findByUsername("Mike.Johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerRepo.save(any(TrainerEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(trainerMapper.toTrainerModel(any(TrainerEntity.class))).thenReturn(trainerModel);

        Trainer updatedTrainer = Trainer.builder()
                .firstName("Mike")
                .lastName("Johnson")
                .specialization("Fitness")
                .build();

        Trainer result = trainerService.updateTrainer("Mike.Johnson", updatedTrainer);

        assertNotNull(result);
        verify(trainerMapper).updateEntity(updatedTrainer, trainerEntity);
        verify(trainerRepo).save(trainerEntity);

        // username should remain the same
        assertEquals("Mike.Johnson", trainerEntity.getUserEntity().getUsername());
        verify(userRepo, never()).findByUsername(anyString()); // generateUniqueUsername should NOT be called
    }

    @Test
    void updateTrainer_success_nameChanged_shouldRegenerateUsername() {
        when(trainerRepo.findByUsername("Mike.Johnson")).thenReturn(Optional.of(trainerEntity));

        // when name changes, service calls generateUniqueUsername(base)
        when(userRepo.findByUsername("New.Name")).thenReturn(Optional.empty());

        // trainerRepo.save returns entity with updated username (same object in this case)
        when(trainerRepo.save(any(TrainerEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        Trainer mappedResult = Trainer.builder()
                .firstName("New")
                .lastName("Name")
                .specialization("Fitness")
                .build();
        when(trainerMapper.toTrainerModel(any(TrainerEntity.class))).thenReturn(mappedResult);

        Trainer updatedTrainer = Trainer.builder()
                .firstName("New")
                .lastName("Name")
                .specialization("Fitness")
                .build();

        Trainer result = trainerService.updateTrainer("Mike.Johnson", updatedTrainer);

        assertNotNull(result);
        verify(trainerMapper).updateEntity(updatedTrainer, trainerEntity);
        verify(userRepo).findByUsername("New.Name");
        verify(trainerRepo).save(trainerEntity);

        assertEquals("New.Name", trainerEntity.getUserEntity().getUsername());
    }

    @Test
    void updateTrainer_notFound() {
        when(trainerRepo.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.updateTrainer("Mike.Johnson", trainerModel));
    }

    @Test
    void getAllTrainers_success() {
        when(trainerRepo.findAll()).thenReturn(List.of(trainerEntity));
        when(trainerMapper.toTrainerModels(anyList())).thenReturn(List.of(trainerModel));

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
        verify(trainerRepo).findAll();
        verify(trainerMapper).toTrainerModels(anyList());
    }
}
