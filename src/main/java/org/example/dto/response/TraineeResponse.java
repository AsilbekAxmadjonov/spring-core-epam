package org.example.dto.response;

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
@Schema(description = "Response object containing trainee profile information")
public class TraineeResponse {

    @Schema(description = "First name of the trainee", example = "John")
    private String firstName;

    @Schema(description = "Last name of the trainee", example = "Doe")
    private String lastName;

    @Schema(description = "Unique username of the trainee", example = "john.doe")
    private String username;

    @Schema(description = "Date of birth of the trainee", example = "1995-06-15", format = "date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Schema(description = "Residential address of the trainee",
            example = "123 Main Street, New York, NY 10001")
    private String address;

    @Schema(description = "Indicates whether the trainee account is active", example = "true")
    private Boolean isActive;

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private Boolean success;

    @Schema(description = "Success or informational message about the trainee operation",
            example = "Trainee profile created successfully")
    private String message;

    @Schema(description = "JWT authentication token for the trainee",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}