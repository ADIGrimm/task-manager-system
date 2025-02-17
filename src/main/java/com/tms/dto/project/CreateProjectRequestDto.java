package com.tms.dto.project;

import com.tms.model.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateProjectRequestDto(
        @NotBlank
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        @NotNull
        Project.ProjectStatus status
) {
}
