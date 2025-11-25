package org.example.entity;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "training")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Trainee is required")
    @ManyToOne
    @JoinColumn(name = "trainee_id", nullable = false)
    private Trainee trainee;

    @NotNull(message = "Trainer is required")
    @ManyToOne
    @JoinColumn(name = "trainer_id", nullable = false)
    private Trainer trainer;

    @NotBlank(message = "Training name cannot be blank")
    @Size(min = 2, max = 100, message = "Training name must be between 2 and 100 characters")
    @Column(nullable = false)
    private String trainingName;

    @NotNull(message = "Training type is required")
    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingType trainingType;

    @NotNull(message = "Training date is required")
    @Column(nullable = false)
    private LocalDate trainingDate;

    @NotNull(message = "Training duration is required")
    @Min(value = 20, message = "Training duration must be at least 1 minute")
    @Max(value = 600, message = "Training duration cannot exceed 600 minutes")
    @Column(nullable = false)
    private Integer trainingDurationMinutes;
}

