package com.challenge.mapper;

import com.challenge.entity.UserEntity;
import com.challenge.model.UserRegisterDTO;
import com.challenge.model.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "password", target = "userPassword")
    @Mapping(target = "userId", ignore = true)
    UserEntity toEntity(UserRegisterDTO userRegisterDTO);

    @Mapping(source = "userPassword", target = "password")
    UserRegisterDTO toDTO(UserEntity user);

    //@Mapping()
    UserResponseDTO toResponseDTO(UserEntity user);
}
