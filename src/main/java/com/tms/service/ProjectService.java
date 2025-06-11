package com.tms.service;

import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto save(Long userId, CreateProjectRequestDto requestDto);

    Page<ProjectDto> getAll(Long userId, Pageable pageable);

    ProjectDto getById(Long userId, Long projectId);

    ProjectDto update(Long userId, Long projectId, CreateProjectRequestDto requestDto);

    void deleteById(Long userId, Long projectId);
}
