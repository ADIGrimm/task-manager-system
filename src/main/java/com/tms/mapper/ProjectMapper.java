package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import com.tms.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toModel(CreateProjectRequestDto requestDto);

    void updateProject(CreateProjectRequestDto requestDtoDto, @MappingTarget Project project);
}
