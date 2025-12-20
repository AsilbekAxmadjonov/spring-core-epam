package org.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request object for creating or updating a training type")
public class TrainingTypeRequest {

    @Schema(description = "Name of the training type/specialization",
            example = "Yoga",
            required = true,
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    @NotBlank(message = "Training type name is required")
    @Size(min = 2, max = 100, message = "Training type name must be between 2 and 100 characters")
    private String trainingTypeName;
}