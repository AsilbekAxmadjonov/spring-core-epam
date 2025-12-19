package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.request.TraineeRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.TraineeResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.Trainee;
import org.example.services.TraineeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainees", description = "Trainee management endpoints - ALL PUBLIC (no authentication required)")
public class TraineeController {

    private final TraineeService traineeService;
    @Operation(
            summary = "Register a new trainee",
            description = "Public endpoint to register a new trainee. Username and password are auto-generated. " +
                    "Only firstName and lastName are required. Date of birth and address are optional."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainee created successfully with generated username and password",
                    content = @Content(schema = @Schema(implementation = TraineeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping
    public ResponseEntity<TraineeResponse> createTrainee(@Valid @RequestBody TraineeRequest request) {
        log.info("Creating new trainee: {} {}", request.getFirstName(), request.getLastName());

        Trainee trainee = Trainee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .isActive(true)
                .build();

        Trainee created = traineeService.createTrainee(trainee);

        log.info("Trainee created successfully with username: {}", created.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TraineeResponse.builder()
                        .username(created.getUsername())
                        .password(created.getPassword())
                        .token(created.getToken())
                        .build());
    }

    @Operation(
            summary = "Get trainee by username",
            description = "Retrieve detailed information about a specific trainee. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<Trainee> getTraineeByUsername(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable("username") String username) {
        log.info("Fetching trainee by username: {}", username);

        return traineeService.getTraineeByUsername(username)
                .map(trainee -> {
                    log.info("Trainee found: {}", username);
                    return ResponseEntity.ok(trainee);
                })
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new UserNotFoundException("Trainee not found with username: " + username);
                });
    }

    @Operation(
            summary = "Get all trainees",
            description = "Retrieve a list of all registered trainees. No authentication required."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of trainees retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<Trainee>> getAllTrainees() {
        log.info("Fetching all trainees");

        List<Trainee> trainees = traineeService.getAllTrainees();

        log.info("Fetched {} trainees", trainees.size());

        return ResponseEntity.ok(trainees);
    }

    @Operation(
            summary = "Update trainee information",
            description = "Update personal information for an existing trainee. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee updated successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PutMapping("/{username}")
    public ResponseEntity<Trainee> updateTrainee(
            @PathVariable("username") String username,
            @Valid @RequestBody TraineeRequest request) {

        log.info("Updating trainee: {}", username);

        Trainee trainee = Trainee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        Trainee updated = traineeService.updateTrainee(username, trainee);

        log.info("Trainee updated successfully: {}", username);

        return ResponseEntity.ok(updated);
    }

    @Operation(
            summary = "Delete trainee",
            description = "Remove a trainee from the system. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Trainee deleted successfully"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable("username") String username) {
        log.info("Deleting trainee: {}", username);

        traineeService.deleteTraineeByUsername(username);

        log.info("Trainee deleted successfully: {}", username);

        return ResponseEntity.noContent().build();
    }
}