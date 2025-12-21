package org.example.persistance.repository;

import org.example.persistance.entity.LoginAttemptEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface LoginAttemptRepo extends JpaRepository<LoginAttemptEntity, Long> {

    Optional<LoginAttemptEntity> findByUsername(String username);

    @Modifying
    @Query("DELETE FROM LoginAttemptEntity l WHERE l.blockedUntil < :now AND l.isBlocked = true")
    void deleteExpiredBlocks(LocalDateTime now);
}