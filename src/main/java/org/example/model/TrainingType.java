package org.example.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "trainingTypeName")
public class TrainingType {
    private String trainingTypeName;
}
