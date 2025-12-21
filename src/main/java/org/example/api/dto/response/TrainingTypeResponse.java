package org.example.api.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Response object containing training type information")
public class TrainingTypeResponse {

    @Schema(description = "Name of the training type/specialization",
            example = "Yoga",
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    private String trainingTypeName;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private Boolean success;

    @Schema(description = "Success or informational message about the operation",
            example = "Training type retrieved successfully")
    private String message;
}