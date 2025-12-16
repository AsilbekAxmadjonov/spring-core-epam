package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response object for trainer registration containing generated credentials")
public class TrainerResponse {

    @Schema(description = "Generated unique username of the trainer", example = "Jane.Smith", required = true)
    private String username;

    @Schema(description = "Generated password for the trainer account", example = "xYz789AbC1", required = true)
    private char[] password;

    @Schema(description = "JWT authentication token for the trainer",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;
}