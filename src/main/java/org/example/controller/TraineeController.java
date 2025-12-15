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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/trainees")
@RequiredArgsConstructor
@Tag(name = "Trainees", description = "Trainee management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TraineeController {

    private final TraineeService traineeService;

    @Operation(
            summary = "Create a new trainee",
            description = "Register a new trainee in the system with personal information"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Trainee created successfully",
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
        log.info("Creating new trainee with username: {}", request.getUsername());

        Trainee trainee = Trainee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .password(request.getPassword())
                .isActive(true)
                .dateOfBirth(request.getDateOfBirth())
                .address(request.getAddress())
                .build();

        Trainee created = traineeService.createTrainee(trainee);

        log.info("Trainee created successfully: {}", created.getUsername());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(created));
    }

    @Operation(
            summary = "Get trainee by username",
            description = "Retrieve detailed information about a specific trainee"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Trainee found",
                    content = @Content(schema = @Schema(implementation = TraineeResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{username}")
    public ResponseEntity<TraineeResponse> getTraineeByUsername(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable String username) {
        log.info("Fetching trainee by username: {}", username);

        return traineeService.getTraineeByUsername(username)
                .map(trainee -> {
                    log.info("Trainee found: {}", username);
                    return ResponseEntity.ok(mapToResponse(trainee));
                })
                .orElseThrow(() -> {
                    log.warn("Trainee not found: {}", username);
                    return new UserNotFoundException("Trainee not found with username: " + username);
                });
    }

    @Operation(
            summary = "Get all trainees",
            description = "Retrieve a list of all registered trainees"
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of trainees retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<TraineeResponse>> getAllTrainees() {
        log.info("Fetching all trainees");

        List<Trainee> trainees = traineeService.getAllTrainees();
        List<TraineeResponse> responses = trainees.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} trainees", responses.size());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{username}")
    public ResponseEntity<TraineeResponse> updateTrainee(
            @PathVariable String username,
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

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<Void> deleteTrainee(@PathVariable String username) {
        log.info("Deleting trainee: {}", username);

        traineeService.deleteTraineeByUsername(username);

        log.info("Trainee deleted successfully: {}", username);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{username}/status")
    public ResponseEntity<TraineeResponse> changeActiveStatus(
            @PathVariable String username,
            @RequestParam boolean isActive) {

        log.info("Changing active status for trainee: {}, active={}", username, isActive);

        Trainee updated = traineeService.setActiveStatus(username, isActive);

        log.info("Active status changed for trainee: {}", username);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    @PatchMapping("/{username}/password")
    public ResponseEntity<TraineeResponse> changePassword(
            @PathVariable String username,
            @Valid @RequestBody TraineeRequest request) {

        log.info("Changing password for trainee: {}", username);

        if (request.getOldPassword() == null || request.getNewPassword() == null) {
            return ResponseEntity.badRequest()
                    .body(TraineeResponse.builder()
                            .success(false)
                            .message("Old password and new password are required")
                            .build());
        }

        // Verify old password
        boolean passwordMatches = traineeService.passwordMatches(username, request.getOldPassword());
        if (!passwordMatches) {
            log.warn("Old password does not match for trainee: {}", username);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(TraineeResponse.builder()
                            .success(false)
                            .message("Old password is incorrect")
                            .build());
        }

        traineeService.changePassword(username, request.getNewPassword());

        log.info("Password changed successfully for trainee: {}", username);

        return ResponseEntity.ok(TraineeResponse.builder()
                .success(true)
                .message("Password changed successfully")
                .build());
    }

    private TraineeResponse mapToResponse(Trainee trainee) {
        return TraineeResponse.builder()
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .isActive(trainee.isActive())
                .build();
    }
}