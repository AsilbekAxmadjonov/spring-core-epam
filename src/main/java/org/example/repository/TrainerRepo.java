package org.example.repository;

import org.example.entity.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepo extends JpaRepository<Trainer, Long> {

    @Query("SELECT tr FROM Trainer tr WHERE tr.user.username = :username")
    Optional<Trainer> findByUsername(@Param("username") String username);
}

