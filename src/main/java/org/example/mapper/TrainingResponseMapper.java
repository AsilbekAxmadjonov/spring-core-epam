package org.example.mapper;

import org.example.api.dto.response.TrainingResponse;
import org.example.persistance.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingResponseMapper {

    @Mapping(source = "trainingType.trainingTypeName", target = "trainingType")
    TrainingResponse toResponse(Training training);
}
