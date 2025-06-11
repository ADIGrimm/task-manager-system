package com.tms.dto.task;

import com.tms.model.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record CreateTaskRequestDto(
        @NotBlank
        String name,
        String description,
        Task.TaskPriority priority,
        Task.TaskStatus status,
        LocalDate dueDate,
        @NotNull
        @Positive
        Long projectId,
        @NotNull
        @Positive
        Long assigneeId
) {
}
