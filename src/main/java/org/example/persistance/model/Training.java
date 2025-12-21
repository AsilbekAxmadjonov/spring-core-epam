package org.example.persistance.model;

import lombok.*;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"trainingName"})
@Builder
public class Training {
    private String traineeUsername;
    private String trainerUsername;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Integer trainingDurationMinutes;
}
