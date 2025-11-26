package org.example.repository;

import org.example.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepo extends JpaRepository<Training, Long> {

    @Query("""
            SELECT t FROM Training t
            WHERE t.trainee.user.username = :username
              AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
              AND (:toDate IS NULL OR t.trainingDate <= :toDate)
              AND (:trainerName IS NULL OR CONCAT(t.trainer.user.firstName, ' ', t.trainer.user.lastName) LIKE %:trainerName%)
              AND (:trainingType IS NULL OR t.trainingType.trainingTypeName = :trainingType)
           """)
    List<Training> findTraineeTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("trainerName") String trainerName,
            @Param("trainingType") String trainingType
    );


    @Query("""
            SELECT t FROM Training t
            WHERE t.trainer.user.username = :username
              AND (:fromDate IS NULL OR t.trainingDate >= :fromDate)
              AND (:toDate IS NULL OR t.trainingDate <= :toDate)
              AND (:traineeName IS NULL OR CONCAT(t.trainee.user.firstName, ' ', t.trainee.user.lastName) LIKE %:traineeName%)
           """)
    List<Training> findTrainerTrainings(
            @Param("username") String username,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate,
            @Param("traineeName") String traineeName
    );
}

