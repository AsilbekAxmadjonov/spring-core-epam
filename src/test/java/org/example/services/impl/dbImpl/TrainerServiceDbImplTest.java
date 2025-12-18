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
import org.example.services.TokenService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

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
        when(userRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(userRepo.save(any(UserEntity.class)))
                .thenReturn(userEntity);

        when(trainingTypeRepo.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.of(trainingType));

        when(trainerRepo.save(any(TrainerEntity.class)))
                .thenReturn(trainerEntity);

        when(tokenService.generateToken(anyString()))
                .thenReturn("jwt-token");

        when(trainerMapper.toTrainerModel(trainerEntity))
                .thenReturn(trainerModel);

        Trainer result = trainerService.createTrainer(trainerModel);

        assertNotNull(result);
        assertNotNull(result.getPassword());
        assertNotNull(result.getToken());

        verify(tokenService).generateToken(anyString());
    }

    @Test
    void createTrainer_trainingTypeNotFound() {
        when(userRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode(anyString()))
                .thenReturn("encoded");

        when(userRepo.save(any()))
                .thenReturn(userEntity);

        when(trainingTypeRepo.findByTrainingTypeName("Fitness"))
                .thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> trainerService.createTrainer(trainerModel));
    }

    @Test
    void getTrainerByUsername_found() {
        when(trainerRepo.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainerEntity));

        when(trainerMapper.toTrainerModel(trainerEntity))
                .thenReturn(trainerModel);

        Optional<Trainer> result =
                trainerService.getTrainerByUsername("Mike.Johnson");

        assertTrue(result.isPresent());
    }

    @Test
    void getTrainerByUsername_notFound() {
        when(trainerRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        Optional<Trainer> result =
                trainerService.getTrainerByUsername("Mike.Johnson");

        assertTrue(result.isEmpty());
    }

    @Test
    void updateTrainer_success() {
        when(trainerRepo.findByUsername("Mike.Johnson"))
                .thenReturn(Optional.of(trainerEntity));

        when(trainerRepo.save(trainerEntity))
                .thenReturn(trainerEntity);

        when(trainerMapper.toTrainerModel(trainerEntity))
                .thenReturn(trainerModel);

        Trainer result =
                trainerService.updateTrainer("Mike.Johnson", trainerModel);

        assertNotNull(result);

        verify(trainerMapper).updateEntity(trainerModel, trainerEntity);
    }

    @Test
    void updateTrainer_notFound() {
        when(trainerRepo.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> trainerService.updateTrainer("Mike.Johnson", trainerModel));
    }

    @Test
    void getAllTrainers_success() {
        when(trainerRepo.findAll())
                .thenReturn(List.of(trainerEntity));

        when(trainerMapper.toTrainerModels(anyList()))
                .thenReturn(List.of(trainerModel));

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(1, result.size());
    }
}
