package org.example.mapper;

import org.example.entity.TrainerEntity;
import org.example.model.Trainer;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, TrainingTypeMapper.class})
public interface TrainerMapper {

    @Mapping(source = "user", target = ".")
    @Mapping(source = "specialization", target = "specialization")
    Trainer toTrainerModel(TrainerEntity trainerEntity);

    @InheritInverseConfiguration
    TrainerEntity toTrainerEntity(Trainer trainerModel);

    List<Trainer> toTrainerModels(List<TrainerEntity> trainerEntityEntities);
}
