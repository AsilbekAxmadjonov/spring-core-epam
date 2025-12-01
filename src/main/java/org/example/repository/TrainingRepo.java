package org.example.repository;

import org.example.entity.TrainingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepo extends JpaRepository<TrainingEntity, Long> {

    @Query("""
            SELECT t FROM TrainingEntity t
            WHERE t.traineeEntity.userEntity.username = :username
              AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
              AND (:toDate IS NULL OR t.trainingDate <= :toDate)
              AND (:trainerName IS NULL OR CONCAT(t.trainerEntity.userEntity.firstName, ' ', t.trainerEntity.userEntity.lastName) LIKE %:trainerName%)
              AND (:trainingType IS NULL OR t.trainingTypeEntity.trainingTypeName = :trainingType)
           """)
    List<TrainingEntity> findTraineeTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );


    @Query("""
            SELECT t FROM TrainingEntity t
            WHERE t.trainerEntity.userEntity.username = :username
              AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
              AND (:toDate IS NULL OR t.trainingDate <= :toDate)
              AND (:traineeName IS NULL OR CONCAT(t.traineeEntity.userEntity.firstName, ' ', t.traineeEntity.userEntity.lastName) LIKE %:traineeName%)
           """)
    List<TrainingEntity> findTrainerTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}

