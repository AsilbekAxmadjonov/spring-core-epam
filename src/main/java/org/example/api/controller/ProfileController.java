package org.example.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.api.dto.request.PasswordChangeRequest;
import org.example.api.dto.response.ErrorResponse;
import org.example.api.dto.response.ProfileResponse;
import org.example.exception.UserNotFoundException;
import org.example.services.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@Tag(name = "Profiles", description = "User profile management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class ProfileController {

    private final ProfileService profileService;
    @Operation(
            summary = "Change user active status",
            description = "Activate or deactivate a user account. Toggles the current status."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Status changed successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authenticated or unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{username}/status")
    public ResponseEntity<ProfileResponse> changeActiveStatus(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable("username") String username) {

        log.info("Changing active status for user: {}", username);

        try {
            boolean newStatus = profileService.toggleUserActiveStatus(username);

            log.info("Active status changed for user: {} to {}", username, newStatus);

            return ResponseEntity.ok(ProfileResponse.builder()
                    .success(true)
                    .message("Active status changed successfully")
                    .username(username)
                    .isActive(newStatus)
                    .build());

        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", username);
            throw e;
        } catch (SecurityException e) {
            log.warn("Unauthorized attempt to change status for user: {}", username);
            throw e;
        }
    }

    @Operation(
            summary = "Change user password",
            description = "Update password for a user account. Requires old password verification."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Password changed successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid old password or missing fields",
                    content = @Content(schema = @Schema(implementation = ProfileResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "User not authenticated or unauthorized",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PatchMapping("/{username}/password")
    public ResponseEntity<ProfileResponse> changePassword(
            @Parameter(description = "Username of the user", required = true)
            @PathVariable("username") String username,
            @Valid @RequestBody PasswordChangeRequest request) {

        log.info("Changing password for user: {}", username);


        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            log.warn("Missing old or new password for user: {}", username);
            return ResponseEntity.badRequest()
                    .body(ProfileResponse.builder()
                            .success(false)
                            .message("Old password and new password are required")
                            .username(username)
                            .build());
        }

        try {
            boolean passwordMatches = profileService.passwordMatches(username, request.getOldPassword());

            if (!passwordMatches) {
                log.warn("Old password does not match for user: {}", username);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(ProfileResponse.builder()
                                .success(false)
                                .message("Old password is incorrect")
                                .username(username)
                                .build());
            }

            profileService.changePassword(username, request.getNewPassword());

            log.info("Password changed successfully for user: {}", username);

            return ResponseEntity.ok(ProfileResponse.builder()
                    .success(true)
                    .message("Password changed successfully")
                    .username(username)
                    .build());

        } catch (UserNotFoundException e) {
            log.warn("User not found: {}", username);
            throw e;
        } catch (SecurityException e) {
            log.warn("Unauthorized attempt to change password for user: {}", username);
            throw e;
        } catch (Exception e) {
            log.error("Error changing password for user: {}", username, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ProfileResponse.builder()
                            .success(false)
                            .message("An error occurred while changing password")
                            .username(username)
                            .build());
        }
    }
}