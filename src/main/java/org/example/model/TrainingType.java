package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor  // << needed for Jackson
@AllArgsConstructor
@ToString
@Builder
public class TrainingType {
    @JsonProperty("typeName")
    private String trainingTypeName;
}
