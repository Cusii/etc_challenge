package com.challenge.mapper;

import com.challenge.entity.TaskEntity;
import com.challenge.model.TaskDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

   // @Mapping(source = "taskId", target = "id")
    TaskEntity toEntity(TaskDTO taskDTO);

    TaskDTO toDTO(TaskEntity taskEntity);
}
