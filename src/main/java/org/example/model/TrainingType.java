package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TrainingType {
    @JsonProperty("typeName")
    private String trainingTypeName;

    public TrainingType() {}

    public TrainingType(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    public String getTrainingTypeName() {
        return trainingTypeName;
    }
    public void setTrainingTypeName(String trainingTypeName) {
        this.trainingTypeName = trainingTypeName;
    }

    @Override
    public String toString() {
        return trainingTypeName;
    }
}
