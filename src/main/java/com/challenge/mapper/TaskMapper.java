package com.challenge.mapper;

import com.challenge.entity.Tasks;
import com.challenge.model.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

   // @Mapping(source = "taskId", target = "id")
    Tasks toEntity(TaskDTO taskDTO);

    TaskDTO toDTO(Tasks tasks);
}
