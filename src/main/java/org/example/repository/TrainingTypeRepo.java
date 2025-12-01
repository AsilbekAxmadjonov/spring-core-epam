package org.example.repository;

import org.example.entity.TrainingTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrainingTypeRepo extends JpaRepository<TrainingTypeEntity, Long> {

    Optional<TrainingTypeEntity> findByName(String name);

}


