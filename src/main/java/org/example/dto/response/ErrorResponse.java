package org.example.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error response containing details about failed operations")
public class ErrorResponse {

    private LocalDateTime timestamp;

    @Schema(description = "HTTP status code of the error", example = "400")
    private int status;

    @Schema(description = "Error type or category", example = "Bad Request")
    private String error;

    @Schema(description = "Detailed error message explaining what went wrong",
            example = "Validation failed for the provided input")
    private String message;

    @Schema(description = "Map of field-specific validation errors (field name as key, error message as value)",
            example = "{\"username\": \"Username must be between 3 and 50 characters\", \"password\": \"Password is required\"}")
    private Map<String, String> validationErrors;

    @Schema(
            description = "Request path where the error occurred",
            example = "/api/trainers"
    )
    private String path;


}