package org.example.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object for trainee registration containing generated credentials")
public class TraineeResponse {

    @Schema(description = "Generated unique username of the trainee", example = "John.Doe", required = true)
    private String username;

    @Schema(description = "Generated password for the trainee account", example = "aBc123XyZ4", required = true)
    private char[] password;

    @Schema(description = "JWT authentication token for the trainee",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}