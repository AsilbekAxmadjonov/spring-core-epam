package org.example.api.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object containing training session information")
public class TrainingResponse {

    @Schema(description = "Username of the trainee participating in the training",
            example = "john.doe")
    private String traineeUsername;

    @Schema(description = "Username of the trainer conducting the training",
            example = "jane.smith")
    private String trainerUsername;

    @Schema(description = "Name or title of the training session",
            example = "Morning Yoga Session")
    private String trainingName;

    @Schema(description = "Type/specialization of the training",
            example = "Yoga",
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    private String trainingType;

    @Schema(description = "Date when the training session was/is scheduled",
            example = "2024-12-20",
            format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @Schema(description = "Duration of the training session in minutes",
            example = "60",
            minimum = "1",
            maximum = "600")
    private Integer trainingDurationMinutes;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private Boolean success;

    @Schema(description = "Success or informational message about the operation",
            example = "Training session created successfully")
    private String message;
}