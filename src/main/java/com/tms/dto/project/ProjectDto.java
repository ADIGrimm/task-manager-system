package com.tms.dto.project;

import com.tms.model.Project;
import java.time.LocalDate;

public record ProjectDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Project.ProjectStatus status
) {
}
