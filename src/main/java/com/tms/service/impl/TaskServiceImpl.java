package com.tms.service.impl;

import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.TaskMapper;
import com.tms.model.Task;
import com.tms.repository.project.ProjectRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final DropboxService dropboxService;

    @Override
    public TaskDto save(Long userId, CreateTaskRequestDto requestDto) {
        Task task = taskMapper.toModel(requestDto);
        task.setAssignee(userRepository.findById(requestDto.assigneeId()).orElseThrow(
                () -> new EntityNotFoundException(
                        "User not found with id: " + requestDto.assigneeId()
                )));
        task.setProject(projectRepository.findByIdAndUserId(requestDto.projectId(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Project not found with id: " + requestDto.projectId()
                )));
        taskRepository.save(task);
        dropboxService.createFolder(
                "projects/" + requestDto.projectId() + "/tasks/" + task.getId()
        );
        return taskMapper.toDto(task);
    }

    @Override
    public Page<TaskDto> getAllFromProject(Long userId, Long projectId, Pageable pageable) {
        return taskRepository.findAllByProjectIdAndUserHasAccess(userId, projectId, pageable).map(taskMapper::toDto);
    }

    @Override
    public TaskDto getById(Long userId, Long taskId) {
        return taskMapper.toDto(taskRepository.findAccessibleTask(taskId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id " + taskId)));
    }

    @Override
    public TaskDto update(Long userId, Long taskId, CreateTaskRequestDto requestDto) {
        Task task = taskRepository.findTaskByIdAndProjectOwner(taskId, userId).orElseThrow(
                () -> new EntityNotFoundException("Can't find task by id " + taskId));
        taskMapper.updateTask(requestDto, task);
        taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    @Override
    public void deleteById(Long userId, Long taskId) {
        Task task = taskRepository.findTaskByIdAndProjectOwner(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find task by id " + taskId));
        dropboxService.deleteFolder("projects/" + task.getProject().getId() + "/tasks/" + taskId);
        taskRepository.deleteById(taskId);
    }
}
