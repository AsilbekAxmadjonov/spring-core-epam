package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "trainer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
public class TrainerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Training specialization is required")
    @ManyToOne
    @JoinColumn(name = "training_type_id", nullable = false)
    private TrainingTypeEntity specialization;

    @NotNull(message = "User cannot be null")
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @OneToMany(mappedBy = "trainerEntity", cascade = CascadeType.ALL)
    private List<TrainingEntity> trainingEntities;
}

