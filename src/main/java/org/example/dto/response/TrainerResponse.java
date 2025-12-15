package org.example.dto.response;

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
@Schema(description = "Response object containing trainer profile information")
public class TrainerResponse {

    @Schema(description = "First name of the trainer", example = "Jane")
    private String firstName;

    @Schema(description = "Last name of the trainer", example = "Smith")
    private String lastName;

    @Schema(description = "Unique username of the trainer", example = "jane.smith")
    private String username;

    @Schema(description = "Training specialization of the trainer",
            example = "Yoga",
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    private String specialization;

    @Schema(description = "Indicates whether the trainer account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private Boolean success;

    @Schema(description = "Success or informational message about the operation",
            example = "Trainer profile updated successfully")
    private String message;
}