package org.example.services.impl.inMemoryImpl;

import org.example.dao.TrainerDao;
import org.example.persistance.model.Trainer;
import org.example.persistance.model.TrainerRegistrationResult;
import org.example.security.AuthenticationContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceInMemoryImplTest {

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private TrainerServiceInMemoryImpl trainerService;

    @Test
    void createTrainer_shouldSaveTrainer_andReturnRegistrationResult() {
        Trainer trainer = createTrainer("john01", "John", "Doe");

        TrainerRegistrationResult result = trainerService.createTrainer(trainer);

        verify(trainerDao, times(1)).save(any(Trainer.class));

        assertNotNull(result);
        assertEquals("john01", result.getUsername());
        assertNotNull(result.getTemporaryPassword());
        assertNull(result.getToken());
    }

    @Test
    void createTrainer_shouldGenerateUsername_whenMissing() {
        Trainer trainer = createTrainer(null, "John", "Doe");

        TrainerRegistrationResult result = trainerService.createTrainer(trainer);

        verify(trainerDao).save(any(Trainer.class));

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
        assertEquals("pass123", result.getTemporaryPassword());
        assertNull(result.getToken());
    }

    @Test
    void getTrainerByUsername_shouldReturnTrainer_whenAuthenticated() {
        String username = "john01";
        Trainer expected = createTrainer(username, "John", "Doe");
        when(trainerDao.findByUsername(username)).thenReturn(expected);

        try (MockedStatic<AuthenticationContext> mocked = mockStatic(AuthenticationContext.class)) {
            mocked.when(AuthenticationContext::getAuthenticatedUser).thenReturn(username);

            Optional<Trainer> result = trainerService.getTrainerByUsername(username);

            assertTrue(result.isPresent());
            assertEquals(username, result.get().getUsername());
            verify(trainerDao).findByUsername(username);
        }
    }

    @Test
    void getTrainerByUsername_shouldThrow_whenNotAuthenticated() {
        String username = "john01";

        try (MockedStatic<AuthenticationContext> mocked = mockStatic(AuthenticationContext.class)) {
            mocked.when(AuthenticationContext::getAuthenticatedUser).thenReturn(null);

            assertThrows(SecurityException.class, () -> trainerService.getTrainerByUsername(username));
            verify(trainerDao, never()).findByUsername(any());
        }
    }

    @Test
    void updateTrainer_shouldUpdate_whenAuthenticated() {
        String username = "john01";
        Trainer trainer = createTrainer(username, "John", "Doe");

        try (MockedStatic<AuthenticationContext> mocked = mockStatic(AuthenticationContext.class)) {
            mocked.when(AuthenticationContext::getAuthenticatedUser).thenReturn(username);

            Trainer updated = trainerService.updateTrainer(username, trainer);

            assertSame(trainer, updated);
            verify(trainerDao).update(trainer);
        }
    }

    @Test
    void updateTrainer_shouldThrow_whenNotAuthenticated() {
        String username = "john01";
        Trainer trainer = createTrainer(username, "John", "Doe");

        try (MockedStatic<AuthenticationContext> mocked = mockStatic(AuthenticationContext.class)) {
            mocked.when(AuthenticationContext::getAuthenticatedUser).thenReturn("someoneElse");

            assertThrows(SecurityException.class, () -> trainerService.updateTrainer(username, trainer));
            verify(trainerDao, never()).update(any());
        }
    }

    @Test
    void getAllTrainers_shouldReturnAllFromDao() {
        List<Trainer> trainers = List.of(
                createTrainer("a", "A", "One"),
                createTrainer("b", "B", "Two")
        );
        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(2, result.size());
        verify(trainerDao).findAll();
    }

    private Trainer createTrainer(String username, String firstName, String lastName) {
        Trainer t = new Trainer();
        t.setUsername(username);
        t.setFirstName(firstName);
        t.setLastName(lastName);
        return t;
    }
}
