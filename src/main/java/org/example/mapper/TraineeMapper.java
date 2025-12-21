package org.example.mapper;

import org.example.persistance.entity.TraineeEntity;
import org.example.persistance.model.Trainee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TraineeMapper {

    @Mapping(source = "userEntity", target = ".")
    Trainee toTraineeModel(TraineeEntity traineeEntity);

    @Mapping(source = ".", target = "userEntity")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    TraineeEntity toTraineeEntity(Trainee traineeModel);

    List<Trainee> toTraineeModels(List<TraineeEntity> traineeEntities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    @Mapping(target = "trainingEntities", ignore = true)
    void updateEntity(Trainee model, @MappingTarget TraineeEntity entity);
}