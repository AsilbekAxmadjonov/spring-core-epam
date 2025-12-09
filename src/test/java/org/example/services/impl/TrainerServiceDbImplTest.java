package org.example.services.impl;

import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.entity.UserEntity;
import org.example.exception.UserNotFoundException;
import org.example.mapper.TrainerMapper;
import org.example.model.Trainer;
import org.example.repository.TrainerRepo;
import org.example.repository.TrainingTypeRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceDbImplTest {

    @Mock
    private TrainerRepo trainerRepo;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingTypeRepo trainingTypeRepo;

    @InjectMocks
    private TrainerServiceDbImpl trainerService;

    private Trainer trainerModel;
    private TrainerEntity trainerEntity;
    private UserEntity userEntity;
    private TrainingTypeEntity trainingType;

    @BeforeEach
    void setUp() {
        userEntity = UserEntity.builder()
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

    @Test
    void createTrainer_shouldCreateAndReturnTrainer() {
        when(trainerMapper.toTrainerEntity(trainerModel)).thenReturn(trainerEntity);
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Trainer result = trainerService.createTrainer(trainerModel);

        assertNotNull(result);
        assertEquals("mike.johnson", result.getUsername());
        assertEquals("Mike", result.getFirstName());
        assertEquals("Fitness", result.getSpecialization());
        verify(trainerMapper).toTrainerEntity(trainerModel);
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainerWhenFound() {
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Optional<Trainer> result = trainerService.getTrainerByUsername("mike.johnson");

        assertTrue(result.isPresent());
        assertEquals("mike.johnson", result.get().getUsername());
        assertEquals("Mike", result.get().getFirstName());
        assertEquals("Fitness", result.get().getSpecialization());
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void getTrainerByUsername_shouldReturnEmptyWhenNotFound() {
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        Optional<Trainer> result = trainerService.getTrainerByUsername("unknown");

        assertFalse(result.isPresent());
        verify(trainerRepo).findByUsername("unknown");
        verify(trainerMapper, never()).toTrainerModel(any());
    }

    @Test
    void updateTrainer_shouldUpdateAndReturnTrainer() {
        Trainer updatedModel = Trainer.builder()
                .firstName("Michael")
                .lastName("Smith")
                .specialization("Yoga")
                .build();

        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(updatedModel);

        Trainer result = trainerService.updateTrainer("mike.johnson", updatedModel);

        assertNotNull(result);
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerMapper).updateEntity(updatedModel, trainerEntity);
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void updateTrainer_shouldThrowExceptionWhenTrainerNotFound() {
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                trainerService.updateTrainer("unknown", trainerModel)
        );
        verify(trainerRepo).findByUsername("unknown");
        verify(trainerMapper, never()).updateEntity(any(), any());
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void passwordMatches_shouldReturnTrueWhenPasswordMatches() {
        char[] password = "password123".toCharArray();
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));

        boolean result = trainerService.passwordMatches("mike.johnson", password);

        assertTrue(result);
        verify(trainerRepo).findByUsername("mike.johnson");
    }

    @Test
    void passwordMatches_shouldReturnFalseWhenPasswordDoesNotMatch() {
        char[] wrongPassword = "wrongpassword".toCharArray();
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));

        boolean result = trainerService.passwordMatches("mike.johnson", wrongPassword);

        assertFalse(result);
        verify(trainerRepo).findByUsername("mike.johnson");
    }

    @Test
    void passwordMatches_shouldReturnFalseWhenTrainerNotFound() {
        char[] password = "password123".toCharArray();
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        boolean result = trainerService.passwordMatches("unknown", password);

        assertFalse(result);
        verify(trainerRepo).findByUsername("unknown");
    }

    @Test
    void changePassword_shouldUpdatePasswordAndReturnTrainer() {
        char[] newPassword = "newPassword456".toCharArray();
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Trainer result = trainerService.changePassword("mike.johnson", newPassword);

        assertNotNull(result);
        assertArrayEquals(newPassword, trainerEntity.getUserEntity().getPassword());
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void changePassword_shouldThrowExceptionWhenTrainerNotFound() {
        char[] newPassword = "newPassword456".toCharArray();
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                trainerService.changePassword("unknown", newPassword)
        );
        verify(trainerRepo).findByUsername("unknown");
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void setActiveStatus_shouldActivateTrainer() {
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Trainer result = trainerService.setActiveStatus("mike.johnson", true);

        assertNotNull(result);
        assertTrue(trainerEntity.getUserEntity().getIsActive());
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void setActiveStatus_shouldDeactivateTrainer() {
        when(trainerRepo.findByUsername("mike.johnson")).thenReturn(Optional.of(trainerEntity));
        when(trainerRepo.save(trainerEntity)).thenReturn(trainerEntity);
        when(trainerMapper.toTrainerModel(trainerEntity)).thenReturn(trainerModel);

        Trainer result = trainerService.setActiveStatus("mike.johnson", false);

        assertNotNull(result);
        assertFalse(trainerEntity.getUserEntity().getIsActive());
        verify(trainerRepo).findByUsername("mike.johnson");
        verify(trainerRepo).save(trainerEntity);
        verify(trainerMapper).toTrainerModel(trainerEntity);
    }

    @Test
    void setActiveStatus_shouldThrowExceptionWhenTrainerNotFound() {
        when(trainerRepo.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                trainerService.setActiveStatus("unknown", true)
        );
        verify(trainerRepo).findByUsername("unknown");
        verify(trainerRepo, never()).save(any());
    }

    @Test
    void getAllTrainers_shouldReturnAllTrainers() {
        UserEntity user2 = UserEntity.builder()
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
