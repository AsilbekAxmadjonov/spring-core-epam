package org.example.mapper;

import org.example.entity.TrainingEntity;
import org.example.model.Training;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = TrainingTypeMapper.class)
public interface TrainingMapper {
    @Mapping(source = "trainee.user.username", target = "traineeUsername")
    @Mapping(source = "trainer.user.username", target = "trainerUsername")
    @Mapping(source = "trainingType", target = "trainingType")
    Training toTrainingModel(TrainingEntity trainingEntity);

    @InheritInverseConfiguration
    TrainingEntity toTrainingEntity(Training trainingModel);

    List<Training> toTrainingModels(List<TrainingEntity> trainingEntityEntities);
}


