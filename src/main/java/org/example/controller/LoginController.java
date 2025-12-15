package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.LoginRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.LoginResponse;
import org.example.model.User;
import org.example.services.TokenService;
import org.example.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class LoginController {

    private final UserService userService;
    private final TokenService tokenService;

    @Operation(
            summary = "User login",
            description = "Authenticate user with username and password. Returns JWT token on successful authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid credentials",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Account is inactive",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for username: {}", loginRequest.getUsername());

        try {
            // Authenticate user
            User user = userService.authenticate(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            // Check if account is active
            if (!user.isActive()) {
                log.warn("Login attempt for inactive account: {}", loginRequest.getUsername());
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(LoginResponse.builder()
                                .success(false)
                                .message("Account is inactive")
                                .build());
            }

            // Generate token
            String token = tokenService.generateToken(user.getUsername());

            log.info("Login successful for username: {}", loginRequest.getUsername());

            return ResponseEntity.ok(LoginResponse.builder()
                    .success(true)
                    .message("Login successful")
                    .token(token)
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .build());

        } catch (BadCredentialsException e) {
            log.warn("Invalid credentials for username: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(LoginResponse.builder()
                            .success(false)
                            .message("Invalid username or password")
                            .build());
        } catch (Exception e) {
            log.error("Login error for username: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(LoginResponse.builder()
                            .success(false)
                            .message("An error occurred during login")
                            .build());
        }
    }

    @Operation(
            summary = "User logout",
            description = "Logout the current user. Since JWT is stateless, this mainly serves as a confirmation endpoint.",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Logout successful",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid or missing token",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(@RequestHeader("Authorization") String token) {
        log.info("Logout request received");

        // Since JWT is stateless, logout is typically handled on the client side
        // by removing the token. You can implement token blacklisting if needed.

        return ResponseEntity.ok(LoginResponse.builder()
                .success(true)
                .message("Logout successful")
                .build());
    }
}