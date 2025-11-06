package org.example.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "trainingTypeName")
@ToString
@Builder
public class TrainingType {
    private String trainingTypeName;
}
