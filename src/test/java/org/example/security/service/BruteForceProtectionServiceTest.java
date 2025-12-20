package org.example.security.service;

import org.example.exception.UserBlockedException;
import org.example.persistance.entity.LoginAttemptEntity;
import org.example.persistance.repository.LoginAttemptRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BruteForceProtectionServiceTest {

    @Mock
    private LoginAttemptRepo loginAttemptRepo;

    @InjectMocks
    private BruteForceProtectionService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void checkIfBlocked_userNotBlocked_noException() {
        when(loginAttemptRepo.findByUsername("user1")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> service.checkIfBlocked("user1"));
    }

    @Test
    void checkIfBlocked_userBlocked_throwsException() {
        LocalDateTime future = LocalDateTime.now().plusMinutes(3);
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setUsername("user2");
        attempt.setIsBlocked(true);
        attempt.setBlockedUntil(future);

        when(loginAttemptRepo.findByUsername("user2")).thenReturn(Optional.of(attempt));

        UserBlockedException ex = assertThrows(UserBlockedException.class,
                () -> service.checkIfBlocked("user2"));

        assertTrue(ex.getMessage().contains("Account is blocked"));
    }

    @Test
    void recordFailedAttempt_newUser_attemptCount1() {
        when(loginAttemptRepo.findByUsername("user3")).thenReturn(Optional.empty());

        service.recordFailedAttempt("user3");

        ArgumentCaptor<LoginAttemptEntity> captor = ArgumentCaptor.forClass(LoginAttemptEntity.class);
        verify(loginAttemptRepo).save(captor.capture());

        assertEquals("user3", captor.getValue().getUsername());
        assertEquals(1, captor.getValue().getAttemptCount());
        assertFalse(captor.getValue().getIsBlocked());
    }

    @Test
    void recordFailedAttempt_reachesMax_attemptsBlocked() {
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setUsername("user4");
        attempt.setAttemptCount(2);
        attempt.setIsBlocked(false);
        attempt.setLastAttemptTime(LocalDateTime.now());

        when(loginAttemptRepo.findByUsername("user4")).thenReturn(Optional.of(attempt));

        service.recordFailedAttempt("user4");

        ArgumentCaptor<LoginAttemptEntity> captor = ArgumentCaptor.forClass(LoginAttemptEntity.class);
        verify(loginAttemptRepo).save(captor.capture());

        LoginAttemptEntity saved = captor.getValue();
        assertTrue(saved.getIsBlocked());
        assertEquals(3, saved.getAttemptCount());
        assertNotNull(saved.getBlockedUntil());
    }

    @Test
    void resetAttempts_resetsAttemptCountAndBlock() {
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setUsername("user5");
        attempt.setAttemptCount(2);
        attempt.setIsBlocked(true);

        when(loginAttemptRepo.findByUsername("user5")).thenReturn(Optional.of(attempt));

        service.resetAttempts("user5");

        ArgumentCaptor<LoginAttemptEntity> captor = ArgumentCaptor.forClass(LoginAttemptEntity.class);
        verify(loginAttemptRepo).save(captor.capture());

        LoginAttemptEntity saved = captor.getValue();
        assertEquals(0, saved.getAttemptCount());
        assertFalse(saved.getIsBlocked());
        assertNull(saved.getBlockedUntil());
    }

    @Test
    void getRemainingAttempts_userNotExist_returnsMaxAttempts() {
        when(loginAttemptRepo.findByUsername("user6")).thenReturn(Optional.empty());
        assertEquals(3, service.getRemainingAttempts("user6"));
    }

    @Test
    void getRemainingAttempts_userBlocked_returnsZero() {
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setIsBlocked(true);
        when(loginAttemptRepo.findByUsername("user7")).thenReturn(Optional.of(attempt));

        assertEquals(0, service.getRemainingAttempts("user7"));
    }

    @Test
    void getRemainingAttempts_someAttempts_remainingCalculated() {
        LoginAttemptEntity attempt = new LoginAttemptEntity();
        attempt.setIsBlocked(false);
        attempt.setAttemptCount(1);
        when(loginAttemptRepo.findByUsername("user8")).thenReturn(Optional.of(attempt));

        assertEquals(2, service.getRemainingAttempts("user8"));
    }
}

