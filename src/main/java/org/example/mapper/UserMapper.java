package org.example.mapper;

import org.example.persistance.entity.UserEntity;
import org.example.persistance.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toModel(UserEntity userEntity);

    UserEntity toEntity(User userModel);

    List<User> toModels(List<UserEntity> userEntityEntities);

    void updateEntityFromModel(User source, @MappingTarget UserEntity target);
}
