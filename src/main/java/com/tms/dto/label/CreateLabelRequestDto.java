package com.tms.dto.label;

import com.tms.validation.color.HtmlColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateLabelRequestDto(
        @NotBlank
        String name,
        @NotBlank
        @HtmlColor
        String color,
        @NotNull
        @Positive
        Long taskId
) {
}
