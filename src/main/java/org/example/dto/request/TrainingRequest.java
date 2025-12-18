package org.example.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating a training session or filtering training records")
public class TrainingRequest {

    @Schema(description = "Username of the trainee participating in the training",
            example = "john.doe",
            required = true)
    @NotBlank(message = "Trainee username is required")
    private String traineeUsername;

    @Schema(description = "Username of the trainer conducting the training",
            example = "jane.smith",
            required = true)
    @NotBlank(message = "Trainer username is required")
    private String trainerUsername;

    @Schema(description = "Name or title of the training session",
            example = "Morning Yoga Session",
            required = true)
    @NotBlank(message = "Training name is required")
    @Size(min = 2, max = 100, message = "Training name must be between 2 and 100 characters")
    private String trainingName;

    @Schema(description = "Date when the training session is scheduled",
            example = "2024-12-20",
            format = "date",
            required = true)
    @NotNull(message = "Training date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate trainingDate;

    @Schema(description = "Duration of the training session in minutes",
            example = "60",
            minimum = "1",
            maximum = "600",
            required = true)
    @NotNull(message = "Training duration is required")
    @Min(value = 1, message = "Training duration must be at least 1 minute")
    @Max(value = 600, message = "Training duration cannot exceed 600 minutes")
    private Integer trainingDurationMinutes;

    @Schema(description = "Start date for filtering training records (used in query operations)",
            example = "2024-01-01",
            format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @Schema(description = "End date for filtering training records (used in query operations)",
            example = "2024-12-31",
            format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    @Schema(description = "Trainer name for filtering training records",
            example = "Jane Smith")
    private String trainerName;

    @Schema(description = "Trainee name for filtering training records",
            example = "John Doe")
    private String traineeName;

    @Schema(description = "Training type/specialization for filtering training records",
            example = "Yoga",
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    private String trainingType;
}