package com.tms.dto.task;

import com.tms.model.Task;
import java.time.LocalDate;

public record TaskDto(
        Long id,
        String name,
        String description,
        Task.TaskPriority priority,
        Task.TaskStatus status,
        LocalDate dueDate,
        Long projectId,
        Long assigneeId
) {
}
