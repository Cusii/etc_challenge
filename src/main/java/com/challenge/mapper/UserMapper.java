package com.challenge.mapper;

import com.challenge.entity.Users;
import com.challenge.model.UserRegisterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "password", target = "userPassword")
    @Mapping(target = "userId", ignore = true)
    Users toEntity(UserRegisterDTO userRegisterDTO);

    @Mapping(source = "userPassword", target = "password")
    UserRegisterDTO toDTO(Users user);
}
