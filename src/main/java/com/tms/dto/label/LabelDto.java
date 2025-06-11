package com.tms.dto.label;

public record LabelDto(
        Long id,
        String name,
        String color,
        Long taskId
) {
}
