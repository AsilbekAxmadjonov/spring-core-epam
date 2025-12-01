package org.example.repository;

import org.example.entity.TrainingTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingTypeRepo extends JpaRepository<TrainingTypeEntity, Long> {
}

