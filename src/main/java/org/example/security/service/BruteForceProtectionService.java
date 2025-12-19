package org.example.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.LoginAttemptEntity;
import org.example.exception.UserBlockedException;
import org.example.repository.LoginAttemptRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BruteForceProtectionService {

    private final LoginAttemptRepo loginAttemptRepo;

    private static final int MAX_ATTEMPTS = 3;
    private static final int BLOCK_DURATION_MINUTES = 5;

    @Transactional(readOnly = true)
    public void checkIfBlocked(String username) {
        Optional<LoginAttemptEntity> attemptOpt = loginAttemptRepo.findByUsername(username);

        if (attemptOpt.isPresent()) {
            LoginAttemptEntity attempt = attemptOpt.get();

            if (attempt.getIsBlocked()) {
                LocalDateTime now = LocalDateTime.now();

                if (attempt.getBlockedUntil() != null && now.isBefore(attempt.getBlockedUntil())) {
                    long minutesLeft = java.time.Duration.between(now, attempt.getBlockedUntil()).toMinutes();
                    log.warn("User {} is blocked. Time remaining: {} minutes", username, minutesLeft + 1);
                    throw new UserBlockedException(
                            String.format("Account is blocked due to too many failed login attempts. Try again in %d minute(s).",
                                    minutesLeft + 1)
                    );
                } else {
                    log.info("Block expired for user: {}", username);
                }
            }
        }
    }

    @Transactional
    public void recordFailedAttempt(String username) {
        LocalDateTime now = LocalDateTime.now();

        Optional<LoginAttemptEntity> attemptOpt = loginAttemptRepo.findByUsername(username);

        LoginAttemptEntity attempt;

        if (attemptOpt.isPresent()) {
            attempt = attemptOpt.get();

            if (attempt.getIsBlocked() && attempt.getBlockedUntil() != null
                    && now.isAfter(attempt.getBlockedUntil())) {
                log.info("Resetting expired block for user: {}", username);
                attempt.setAttemptCount(1);
                attempt.setIsBlocked(false);
                attempt.setBlockedUntil(null);
            } else {
                attempt.setAttemptCount(attempt.getAttemptCount() + 1);
            }

            attempt.setLastAttemptTime(now);

        } else {
            attempt = new LoginAttemptEntity();
            attempt.setUsername(username);
            attempt.setAttemptCount(1);
            attempt.setLastAttemptTime(now);
            attempt.setIsBlocked(false);
        }

        if (attempt.getAttemptCount() >= MAX_ATTEMPTS) {
            attempt.setIsBlocked(true);
            attempt.setBlockedUntil(now.plusMinutes(BLOCK_DURATION_MINUTES));
            log.warn("User {} blocked after {} failed attempts. Blocked until: {}",
                    username, attempt.getAttemptCount(), attempt.getBlockedUntil());
        } else {
            log.info("Failed login attempt {} of {} for user: {}",
                    attempt.getAttemptCount(), MAX_ATTEMPTS, username);
        }

        loginAttemptRepo.save(attempt);
    }

    @Transactional
    public void resetAttempts(String username) {
        Optional<LoginAttemptEntity> attemptOpt = loginAttemptRepo.findByUsername(username);

        if (attemptOpt.isPresent()) {
            LoginAttemptEntity attempt = attemptOpt.get();

            if (attempt.getAttemptCount() > 0 || attempt.getIsBlocked()) {
                log.info("Resetting login attempts for user: {} (previous attempts: {})",
                        username, attempt.getAttemptCount());
            }

            attempt.setAttemptCount(0);
            attempt.setIsBlocked(false);
            attempt.setBlockedUntil(null);
            attempt.setLastAttemptTime(LocalDateTime.now());

            loginAttemptRepo.save(attempt);
        }
    }

    @Transactional
    public void cleanupExpiredBlocks() {
        LocalDateTime now = LocalDateTime.now();
        loginAttemptRepo.deleteExpiredBlocks(now);
        log.info("Cleaned up expired login blocks");
    }

    @Transactional(readOnly = true)
    public int getRemainingAttempts(String username) {
        Optional<LoginAttemptEntity> attemptOpt = loginAttemptRepo.findByUsername(username);

        if (attemptOpt.isPresent()) {
            LoginAttemptEntity attempt = attemptOpt.get();
            if (attempt.getIsBlocked()) {
                return 0;
            }
            return Math.max(0, MAX_ATTEMPTS - attempt.getAttemptCount());
        }

        return MAX_ATTEMPTS;
    }
}
