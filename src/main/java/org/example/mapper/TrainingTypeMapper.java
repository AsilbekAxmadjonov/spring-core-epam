package org.example.mapper;

import org.example.persistance.entity.TrainingTypeEntity;
import org.example.persistance.model.TrainingType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TrainingTypeMapper {

    TrainingType toModel(TrainingTypeEntity trainingTypeEntity);

    TrainingTypeEntity toEntity(TrainingType trainingTypeModel);

    List<TrainingType> toModels(List<TrainingTypeEntity> trainingTypeEntityEntities);
}
