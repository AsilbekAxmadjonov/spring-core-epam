package org.example.controller;

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
import org.example.dto.request.TrainerRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.TrainerResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainer;
import org.example.services.TrainerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/trainers")
@RequiredArgsConstructor
@Tag(name = "Trainers", description = "Trainer management endpoints. POST (registration) is public, all other endpoints require JWT authentication.")
public class TrainerController {

    private final TrainerService trainerService;

    @Operation(
            summary = "Register a new trainer",
            description = "Public endpoint to register a new trainer. Username and password are auto-generated. " +
                    "Only firstName, lastName, and specialization are required. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainer created successfully with generated username and password",
                    content = @Content(schema = @Schema(implementation = TrainerResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping
    public ResponseEntity<TrainerResponse> createTrainer(@Valid @RequestBody TrainerRequest request) {
        log.info("Creating new trainer: {} {}", request.getFirstName(), request.getLastName());

        Trainer trainer = Trainer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .isActive(true)
                .build();

        Trainer created = trainerService.createTrainer(trainer);

        log.info("Trainer created successfully with username: {}", created.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TrainerResponse.builder()
                        .username(created.getUsername())
                        .password(created.getPassword())
                        .token(created.getToken())
                        .build());
    }

    @Operation(
            summary = "Get trainer by username",
            description = "Retrieve detailed information about a specific trainer. Requires JWT authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer found"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 Protected
    @GetMapping("/{username}")
    public ResponseEntity<Trainer> getTrainerByUsername(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable("username") String username) {
        log.info("Fetching trainer by username: {}", username);

        return trainerService.getTrainerByUsername(username)
                .map(trainer -> {
                    log.info("Trainer found: {}", username);
                    return ResponseEntity.ok(trainer);
                })
                .orElseThrow(() -> {
                    log.warn("Trainer not found: {}", username);
                    return new UserNotFoundException("Trainer not found with username: " + username);
                });
    }

    @Operation(
            summary = "Get all trainers",
            description = "Retrieve a list of all registered trainers. Requires JWT authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "List of trainers retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 Protected
    @GetMapping
    public ResponseEntity<List<Trainer>> getAllTrainers() {
        log.info("Fetching all trainers");

        List<Trainer> trainers = trainerService.getAllTrainers();

        log.info("Fetched {} trainers", trainers.size());

        return ResponseEntity.ok(trainers);
    }

    @Operation(
            summary = "Update trainer profile",
            description = "Update trainer information including name and specialization. Requires JWT authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainer updated successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - JWT token required",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @SecurityRequirement(name = "Bearer Authentication") // 🔒 Protected
    @PutMapping("/{username}")
    public ResponseEntity<Trainer> updateTrainer(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable("username") String username,
            @Valid @RequestBody TrainerRequest request) {

        log.info("Updating trainer: {}", username);

        Trainer trainer = Trainer.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .specialization(request.getSpecialization())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Trainer updated = trainerService.updateTrainer(username, trainer);

        log.info("Trainer updated successfully: {}", username);

        return ResponseEntity.ok(updated);
    }
}