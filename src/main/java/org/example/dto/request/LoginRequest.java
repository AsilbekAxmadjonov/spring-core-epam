package org.example.dto.request;

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
@Schema(description = "Login request containing user credentials")
public class LoginRequest {

    @Schema(description = "Username for authentication", example = "john.doe", required = true)
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Schema(description = "User password", example = "password1234", required = true, format = "password")
    @NotBlank(message = "Password is required")
    @Size(min = 10, message = "Password must be at least 10 characters")
    private char[] password;
}