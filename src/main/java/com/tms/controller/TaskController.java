package com.tms.controller;

import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import com.tms.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class TaskController implements UserContextHelper {
    private final TaskService taskService;

    @Operation(summary = "Create task",
            description = "Create task")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(Authentication authentication,
                              @Valid @RequestBody CreateTaskRequestDto taskDto) {
        return taskService.save(getUserId(authentication), taskDto);
    }

    @Operation(summary = "Get all tasks",
            description = "Return list of project tasks as page")
    @GetMapping("/project/{projectId}")
    public Page<TaskDto> getAllFromProject(Authentication authentication, @PathVariable Long projectId,
                                @ParameterObject @PageableDefault Pageable pageable) {
        return taskService.getAllFromProject(getUserId(authentication), projectId, pageable);
    }

    @Operation(summary = "Get task by id",
            description = "Return task with specified id")
    @GetMapping("/{id}")
    public TaskDto getTaskById(Authentication authentication,
                               @PathVariable Long id) {
        return taskService.getById(getUserId(authentication), id);
    }

    @Operation(summary = "Update task information",
            description = "Update task information")
    @PutMapping("/{id}")
    public TaskDto updateTask(Authentication authentication, @PathVariable Long id,
                                    @Valid @RequestBody CreateTaskRequestDto taskDto) {
        return taskService.update(getUserId(authentication), id, taskDto);
    }

    @Operation(summary = "Delete task by id",
            description = "Delete task by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(Authentication authentication, @PathVariable Long id) {
        taskService.deleteById(getUserId(authentication), id);
    }
}

