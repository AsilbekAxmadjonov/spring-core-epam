package org.example.dto.request;

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
@Schema(description = "Request object for creating or updating a trainer profile")
public class TrainerRequest {

    @Schema(description = "First name of the trainer", example = "Jane", required = true)
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;

    @Schema(description = "Last name of the trainer", example = "Smith", required = true)
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;

    @Schema(description = "Unique username for the trainer", example = "jane.smith", required = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Password for the trainer account (required only for create operation)",
            example = "TrainerPass123!",
            format = "password",
            minLength = 10,
            maxLength = 100)
    @Size(min = 10, max = 100, message = "Password must be between 10 and 100 characters")
    private char[] password;

    @Schema(description = "Training specialization of the trainer",
            example = "Yoga",
            required = true,
            allowableValues = {"Fitness", "Yoga", "Zumba", "Stretching", "Resistance"})
    @NotBlank(message = "Specialization is required")
    private String specialization;

    @Schema(description = "Indicates whether the trainer account is active",
            example = "true")
    private Boolean isActive;

    @Schema(description = "Current password (required for password change operations)",
            example = "OldTrainerPass123!",
            format = "password")
    private char[] oldPassword;

    @Schema(description = "New password (required for password change operations)",
            example = "NewTrainerPass456!",
            format = "password")
    private char[] newPassword;
}