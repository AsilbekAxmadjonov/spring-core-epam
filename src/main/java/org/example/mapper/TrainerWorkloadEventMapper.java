package org.example.mapper;


import org.example.integration.workload.dto.TrainerWorkloadEventRequest;
import org.example.persistance.entity.TrainingEntity;
import org.example.persistance.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainerWorkloadEventMapper {

    @Mapping(target = "trainingId", expression = "java(saved.getId().toString())")
    @Mapping(target = "username", source = "trainerUser.username")
    @Mapping(target = "firstName", source = "trainerUser.firstName")
    @Mapping(target = "lastName", source = "trainerUser.lastName")
    @Mapping(target = "isActive", expression = "java(Boolean.TRUE.equals(trainerUser.getIsActive()))")
    @Mapping(target = "trainingDate", source = "saved.trainingDate")
    @Mapping(target = "trainingDurationMinutes", source = "saved.trainingDurationMinutes")
    @Mapping(target = "actionType", expression = "java(TrainerWorkloadEventRequest.ActionType.ADD)")
    TrainerWorkloadEventRequest toAddEvent(TrainingEntity saved, UserEntity trainerUser);

    @Mapping(target = "trainingId", expression = "java(saved.getId().toString())")
    @Mapping(target = "username", source = "trainerUser.username")
    @Mapping(target = "firstName", source = "trainerUser.firstName")
    @Mapping(target = "lastName", source = "trainerUser.lastName")
    @Mapping(target = "isActive", expression = "java(Boolean.TRUE.equals(trainerUser.getIsActive()))")
    @Mapping(target = "trainingDate", source = "saved.trainingDate")
    @Mapping(target = "trainingDurationMinutes", source = "saved.trainingDurationMinutes")
    @Mapping(target = "actionType", expression = "java(TrainerWorkloadEventRequest.ActionType.DELETE)")
    TrainerWorkloadEventRequest toDeleteEvent(TrainingEntity saved, UserEntity trainerUser);
}

