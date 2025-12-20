package org.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for trainer operations")
public class TrainerRequest {

    @Schema(description = "First name of the trainer", example = "Jane", required = true)
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;

    @Schema(description = "Last name of the trainer", example = "Smith", required = true)
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;

    @Schema(description = "Training specialization of the trainer",
            example = "Yoga",
            required = true,
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Schema(description = "Indicates whether the trainer account is active",
            example = "true")
    private Boolean isActive;
}