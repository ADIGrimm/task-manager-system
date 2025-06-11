package com.tms.service;

import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskDto save(Long userId, CreateTaskRequestDto taskDto);

    Page<TaskDto> getAllFromProject(Long userId, Long projectId, Pageable pageable);

    TaskDto getById(Long userId, Long id);

    TaskDto update(Long userId, Long id, CreateTaskRequestDto taskDto);

    void deleteById(Long userId, Long id);
}
