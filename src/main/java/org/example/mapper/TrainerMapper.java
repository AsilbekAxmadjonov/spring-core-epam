package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.entity.TrainingTypeEntity;
import org.example.model.Trainer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TrainingTypeMapper.class})
public interface TrainerMapper {

    @Mapping(source = "userEntity", target = ".")
    @Mapping(source = "specialization.trainingTypeName", target = "specialization")
    Trainer toTrainerModel(TrainerEntity trainerEntity);

    @Mapping(source = ".", target = "userEntity")
    @Mapping(source = "specialization", target = "specialization", qualifiedByName = "stringToTrainingType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    TrainerEntity toTrainerEntity(Trainer trainerModel);

    List<Trainer> toTrainerModels(List<TrainerEntity> trainerEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "specialization", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    void updateEntity(Trainer model, @MappingTarget TrainerEntity entity);

    @Named("stringToTrainingType")
    default TrainingTypeEntity stringToTrainingType(String specialization) {
        if (specialization == null) {
            return null;
        }
        TrainingTypeEntity entity = new TrainingTypeEntity();
        entity.setTrainingTypeName(specialization);
        return entity;
    }
}