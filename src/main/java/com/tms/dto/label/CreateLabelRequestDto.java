package com.tms.dto.label;

import com.tms.model.Label;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateLabelRequestDto(
        @NotBlank
        String name,
        @NotNull
        Label.Colors color
) {
}
