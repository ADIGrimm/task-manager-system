package com.tms.service.impl;

import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.ProjectMapper;
import com.tms.model.Project;
import com.tms.repository.project.ProjectRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    @Override
    public ProjectDto save(Long userId, CreateProjectRequestDto requestDto) {
        Project project = projectMapper.toModel(requestDto);
        project.setUserId(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)));
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public Page<ProjectDto> getAll(Pageable pageable) {
        return projectRepository.findAll(pageable).map(projectMapper::toDto);
    }

    @Override
    public ProjectDto getById(Long id) {
        return projectMapper.toDto(projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find project by id " + id)));
    }

    @Override
    public ProjectDto update(Long id, CreateProjectRequestDto requestDto) {
        Project project = projectRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find project by id " + id));
        projectMapper.updateProject(requestDto, project);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void deleteById(Long id) {
        projectRepository.deleteById(id);
    }
}
