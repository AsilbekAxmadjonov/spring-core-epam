package org.example.mapper;

import org.example.entity.TrainingTypeEntity;
import org.example.model.TrainingType;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingType toModel(TrainingTypeEntity trainingTypeEntity);

    @InheritInverseConfiguration
    TrainingTypeEntity toEntity(TrainingType trainingTypeModel);

    List<TrainingType> toModels(List<TrainingTypeEntity> trainingTypeEntityEntities);
}
