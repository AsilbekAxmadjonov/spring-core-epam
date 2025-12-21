package org.example.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request object for changing user password")
public class PasswordChangeRequest {

    @NotNull(message = "Old password is required")
    @Size(min = 10, max = 100, message = "Old password must be between 10 and 100 characters")
    @Schema(description = "Current password", example = "OldPass123!", required = true)
    private char[] oldPassword;

    @NotNull(message = "New password is required")
    @Size(min = 10, max = 100, message = "New password must be between 10 and 100 characters")
    @Schema(description = "New password (minimum 10 characters)", example = "NewPass123!", required = true)
    private char[] newPassword;
}