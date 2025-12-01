package org.example.mapper;


import org.example.entity.TraineeEntity;
import org.example.model.Trainee;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TraineeMapper {

    @Mapping(source = "user", target = ".")
    Trainee toTraineeModel(TraineeEntity traineeEntity);

    @InheritInverseConfiguration
    TraineeEntity toTraineeEntity(Trainee traineeModel);

    List<Trainee> toTraineeModels(List<TraineeEntity> traineeEntityEntities);
}
