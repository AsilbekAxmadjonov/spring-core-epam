package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.response.ErrorResponse;
import org.example.dto.response.TrainingTypeResponse;
import org.example.exception.UserNotFoundException;
import org.example.model.TrainingType;
import org.example.services.TrainingTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/training-types")
@RequiredArgsConstructor
@Tag(name = "Training Types", description = "Training type catalog management endpoints")
@SecurityRequirement(name = "Bearer Authentication")
public class TrainingTypeController {

    private final TrainingTypeService trainingTypeService;

    @Operation(
            summary = "Get all training types",
            description = "Retrieve a complete list of all available training types (e.g., Fitness, Yoga, Zumba, Stretching, Resistance)"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved list of training types",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TrainingTypeResponse.class)
            )
    )
    @GetMapping
    public ResponseEntity<List<TrainingTypeResponse>> getAllTrainingTypes() {
        log.info("Fetching all training types");

        List<TrainingType> trainingTypes = trainingTypeService.getAllTrainingTypes();
        List<TrainingTypeResponse> responses = trainingTypes.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        log.info("Fetched {} training types", responses.size());

        return ResponseEntity.ok(responses);
    }

    @Operation(
            summary = "Get training type by name",
            description = "Retrieve a specific training type by its exact name"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Training type found successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TrainingTypeResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Training type not found with the given name",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @GetMapping("/{trainingTypeName}")
    public ResponseEntity<TrainingTypeResponse> getTrainingTypeByName(
            @Parameter(
                    description = "Name of the training type to retrieve (e.g., Yoga, Fitness, Zumba)",
                    required = true,
                    example = "Yoga"
            )
            @PathVariable String trainingTypeName) {
        log.info("Fetching training type by name: {}", trainingTypeName);

        return trainingTypeService.getTrainingTypeByName(trainingTypeName)
                .map(trainingType -> {
                    log.info("Training type found: {}", trainingTypeName);
                    return ResponseEntity.ok(mapToResponse(trainingType));
                })
                .orElseThrow(() -> {
                    log.warn("Training type not found: {}", trainingTypeName);
                    return new UserNotFoundException("Training type not found: " + trainingTypeName);
                });
    }

    private TrainingTypeResponse mapToResponse(TrainingType trainingType) {
        return TrainingTypeResponse.builder()
                .trainingTypeName(trainingType.getTrainingTypeName())
                .build();
    }
}