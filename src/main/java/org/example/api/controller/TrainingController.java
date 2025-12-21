package org.example.api.controller;

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
import org.example.api.dto.request.TrainingRequest;
import org.example.api.dto.response.ErrorResponse;
import org.example.api.dto.response.TrainingResponse;
import org.example.mapper.TrainingResponseMapper;
import org.example.persistance.model.Training;
import org.example.services.TrainingService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@Tag(name = "Trainings", description = "Training session management endpoints - ALL PUBLIC (no authentication required)")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingResponseMapper trainingResponseMapper;

    @Operation(
            summary = "Create a new training session",
            description = "Schedule a new training session between a trainee and trainer. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Training created successfully",
                    content = @Content(schema = @Schema(implementation = TrainingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Trainee or Trainer not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })

    @PostMapping
    public ResponseEntity<TrainingResponse> createTraining(
            @Valid @RequestBody TrainingRequest request) {

        Training created = trainingService.createTraining(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(trainingResponseMapper.toResponse(created));
    }

    @Operation(
            summary = "Get all training sessions",
            description = "Retrieve a list of all training sessions in the system. No authentication required."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of trainings retrieved successfully"
    )

    @GetMapping
    public ResponseEntity<List<TrainingResponse>> getAllTrainings() {
        log.info("Fetching all trainings");

        List<Training> trainings = trainingService.listAll();
        List<TrainingResponse> responses = trainings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} trainings", responses.size());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get trainee's training sessions",
            description = "Retrieve all training sessions for a specific trainee with optional filters. No authentication required."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of trainee's trainings retrieved successfully"
    )

    @GetMapping("/trainee/{username}")
    public ResponseEntity<List<TrainingResponse>> getTraineeTrainings(
            @Parameter(description = "Username of the trainee", required = true)
            @PathVariable("username") String username,
            @Parameter(description = "Filter by start date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "Filter by end date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @Parameter(description = "Filter by trainer name (partial match)")
            @RequestParam(required = false) String trainerName,
            @Parameter(description = "Filter by training type")
            @RequestParam(required = false) String trainingType) {

        log.info("Fetching trainings for trainee: {} with filters - from: {}, to: {}, trainerName: {}, trainingType: {}",
                username, fromDate, toDate, trainerName, trainingType);

        List<Training> trainings = trainingService.getTraineeTrainings(
                username,
                fromDate,
                toDate,
                trainerName,
                trainingType
        );

        List<TrainingResponse> responses = trainings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} trainings for trainee: {}", responses.size(), username);

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get trainer's training sessions",
            description = "Retrieve all training sessions for a specific trainer with optional filters. No authentication required."
    )
    @ApiResponse(
            responseCode = "200",
            description = "List of trainer's trainings retrieved successfully"
    )
    @GetMapping("/trainer/{username}")
    public ResponseEntity<List<TrainingResponse>> getTrainerTrainings(
            @Parameter(description = "Username of the trainer", required = true)
            @PathVariable("username") String username,
            @Parameter(description = "Filter by start date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
            @Parameter(description = "Filter by end date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate,
            @Parameter(description = "Filter by trainee name (partial match)")
            @RequestParam(required = false) String traineeName) {

        log.info("Fetching trainings for trainer: {} with filters - from: {}, to: {}, traineeName: {}",
                username, fromDate, toDate, traineeName);

        List<Training> trainings = trainingService.getTrainerTrainings(
                username,
                fromDate,
                toDate,
                traineeName
        );

        List<TrainingResponse> responses = trainings.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} trainings for trainer: {}", responses.size(), username);

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get training by name",
            description = "Retrieve a specific training session by its name. No authentication required."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Training found",
                    content = @Content(schema = @Schema(implementation = TrainingResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Training not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{trainingName}")
    public ResponseEntity<TrainingResponse> getTrainingByName(
            @Parameter(description = "Name of the training session", required = true)
            @PathVariable("trainingName") String trainingName) {
        log.info("Fetching training by name: {}", trainingName);

        Training training = trainingService.getTraining(trainingName);

        log.info("Training found: {}", trainingName);

        return ResponseEntity.ok(mapToResponse(training));
    }

    private TrainingResponse mapToResponse(Training training) {
        return TrainingResponse.builder()
                .traineeUsername(training.getTraineeUsername())
                .trainerUsername(training.getTrainerUsername())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType() != null ?
                        training.getTrainingType().getTrainingTypeName() : null)
                .trainingDate(training.getTrainingDate())
                .trainingDurationMinutes(training.getTrainingDurationMinutes())
                .build();
    }
}