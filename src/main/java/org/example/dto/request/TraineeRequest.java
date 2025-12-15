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
@Schema(description = "Request object for creating or updating a trainee profile")
public class TraineeRequest {

    @Schema(description = "First name of the trainee", example = "John", required = true)
    @NotBlank(message = "First name is required")
    @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters")
    private String firstName;

    @Schema(description = "Last name of the trainee", example = "Doe", required = true)
    @NotBlank(message = "Last name is required")
    @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters")
    private String lastName;

    @Schema(description = "Unique username for the trainee", example = "john.doe", required = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "Password for the trainee account (minimum 10 characters)",
            example = "SecurePass123!",
            format = "password",
            minLength = 10,
            maxLength = 100)
    @Size(min = 10, max = 100, message = "Password must be between 10 and 100 characters")
    private char[] password;

    @Schema(description = "Date of birth of the trainee (must be in the past)",
            example = "1995-06-15",
            format = "date")
    @Past(message = "Date of birth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Schema(description = "Residential address of the trainee",
            example = "123 Main Street, New York, NY 10001")
    @Size(min = 3, max = 255, message = "Address must be between 3 and 255 characters")
    private String address;

    @Schema(description = "Indicates whether the trainee account is active",
            example = "true")
    private Boolean isActive;

    @Schema(description = "Current password (required for password change operations)",
            example = "OldPassword123!",
            format = "password")
    private char[] oldPassword;

    @Schema(description = "New password (required for password change operations)",
            example = "NewPassword456!",
            format = "password")
    private char[] newPassword;
}