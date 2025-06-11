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
    private final DropboxService dropboxService;

    @Override
    public ProjectDto save(Long userId, CreateProjectRequestDto requestDto) {
        Project project = projectMapper.toModel(requestDto);
        project.setUserId(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)));
        projectRepository.save(project);
        dropboxService.createFolder("projects/" + project.getId());
        return projectMapper.toDto(project);
    }

    @Override
    public Page<ProjectDto> getAll(Long userId, Pageable pageable) {
        return projectRepository.findAllAccessibleToUser(userId, pageable).map(projectMapper::toDto);
    }

    @Override
    public ProjectDto getById(Long userId, Long projectId) {
        return projectMapper.toDto(projectRepository.findAccessibleProject(projectId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find project by id " + projectId + " and user id " + userId
                )));
    }

    @Override
    public ProjectDto update(Long userId, Long projectId, CreateProjectRequestDto requestDto) {
        Project project = projectRepository.findByIdAndUserId(projectId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find project by id " + projectId));
        projectMapper.updateProject(requestDto, project);
        projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    @Override
    public void deleteById(Long userId, Long projectId) {
        if (projectRepository.findByIdAndUserId(projectId, userId).isPresent()) {
            dropboxService.deleteFolder("projects/" + projectId);
            projectRepository.deleteById(projectId);
        } else {
            throw new EntityNotFoundException("Can't find project by id " + projectId + " and user id " + userId);
        }
    }
}
