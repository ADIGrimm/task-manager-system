package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import com.tms.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface TaskMapper {
    @Mapping(source = "task.project.id", target = "projectId")
    @Mapping(source = "task.assignee.id", target = "assigneeId")
    TaskDto toDto(Task task);

    Task toModel(CreateTaskRequestDto requestDto);

    void updateTask(CreateTaskRequestDto requestDto, @MappingTarget Task task);
}
