package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.model.Label;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    @Mapping(source = "task.id", target = "taskId")
    LabelDto toDto(Label label);

    Label toModel(CreateLabelRequestDto requestDto);

    void updateLabel(CreateLabelRequestDto requestDto, @MappingTarget Label label);
}
