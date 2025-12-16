package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for profile operations")
public class ProfileResponse {

    @Schema(description = "Indicates if the operation was successful", example = "true")
    private boolean success;

    @Schema(description = "Response message", example = "Password changed successfully")
    private String message;

    @Schema(description = "Username of the user", example = "john.doe")
    private String username;

    @Schema(description = "Current active status of the user", example = "true")
    private Boolean isActive;
}