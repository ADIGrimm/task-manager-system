package com.tms.service;

import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    ProjectDto save(Long userId, CreateProjectRequestDto requestDto);

    Page<ProjectDto> getAll(Pageable pageable);

    ProjectDto getById(Long id);

    ProjectDto update(Long id, CreateProjectRequestDto requestDto);

    void deleteById(Long id);
}
