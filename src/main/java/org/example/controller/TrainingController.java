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
import org.example.dto.request.TrainingRequest;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.TrainingResponse;
import org.example.entity.TrainingTypeEntity;
import org.example.model.Training;
import org.example.model.TrainingType;
import org.example.repository.TrainingTypeRepo;
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
@Tag(name = "Trainings", description = "Training session management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TrainingController {

    private final TrainingService trainingService;
    private final TrainingTypeRepo trainingTypeRepo;

    @Operation(
            summary = "Create a new training session",
            description = "Schedule a new training session between a trainee and trainer"
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
    public ResponseEntity<TrainingResponse> createTraining(@Valid @RequestBody TrainingRequest request) {
        log.info("Creating new training: {} for trainee: {} with trainer: {}",
                request.getTrainingName(),
                request.getTraineeUsername(),
                request.getTrainerUsername());

        TrainingTypeEntity trainingTypeEntity = trainingTypeRepo
                .findByTrainingTypeName(request.getTrainingType())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid training type: " + request.getTrainingType()));

        TrainingType trainingType = new TrainingType();
        trainingType.setTrainingTypeName(trainingTypeEntity.getTrainingTypeName());
        trainingType.setTrainingTypeName(trainingTypeEntity.getTrainingTypeName());

        Training training = Training.builder()
                .traineeUsername(request.getTraineeUsername())
                .trainerUsername(request.getTrainerUsername())
                .trainingName(request.getTrainingName())
                .trainingDate(request.getTrainingDate())
                .trainingDurationMinutes(request.getTrainingDurationMinutes())
                .trainingType(trainingType)  // Set the TrainingType model
                .build();

        Training created = trainingService.addTraining(training);

        log.info("Training created successfully: {}", created.getTrainingName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mapToResponse(created));
    }

    @Operation(
            summary = "Get all training sessions",
            description = "Retrieve a list of all training sessions in the system"
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
            description = "Retrieve all training sessions for a specific trainee with optional filters"
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
            description = "Retrieve all training sessions for a specific trainer with optional filters"
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
            description = "Retrieve a specific training session by its name"
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