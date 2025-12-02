package org.example.repository;

import org.example.entity.TrainerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TrainerRepo extends JpaRepository<TrainerEntity, Long> {

    @Query("SELECT tr FROM TrainerEntity tr WHERE tr.userEntity.username = :username")
    Optional<TrainerEntity> findByUsername(@Param("username") String username);
}

