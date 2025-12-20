package org.example.mapper;

import org.example.persistance.entity.TrainingEntity;
import org.example.persistance.model.Training;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TrainingTypeMapper.class, TraineeMapper.class, TrainerMapper.class})
public interface TrainingMapper {

    @Mapping(source = "traineeEntity.userEntity.username", target = "traineeUsername")
    @Mapping(source = "trainerEntity.userEntity.username", target = "trainerUsername")
    @Mapping(source = "trainingTypeEntity", target = "trainingType")
    Training toTrainingModel(TrainingEntity trainingEntity);

    @InheritInverseConfiguration
    TrainingEntity toTrainingEntity(Training trainingModel);

    List<Training> toTrainingModels(List<TrainingEntity> trainingEntities);
}