package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class Training {
    private String trainingName;
    @JsonProperty("trainerUsername")
    private String trainerId;

    @JsonProperty("traineeUsername")
    private String traineeId;

    @JsonProperty("trainingType")
    private TrainingType trainingType;

    @JsonProperty("startDate")
    private LocalDate trainingDate;

    @JsonProperty("duration")
    private int trainingDuration;
}
