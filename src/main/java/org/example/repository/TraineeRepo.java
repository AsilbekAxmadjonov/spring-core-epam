package org.example.repository;

import org.example.entity.TraineeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TraineeRepo extends JpaRepository<TraineeEntity, Long> {

    @Query("SELECT t FROM TraineeEntity t WHERE t.userEntity.username = :username")
    Optional<TraineeEntity> findByUsername(@Param("username") String username);

    @Modifying
    @Query("DELETE FROM TraineeEntity t WHERE t.userEntity.username = :username")
    void deleteByUsername(@Param("username") String username);
}

