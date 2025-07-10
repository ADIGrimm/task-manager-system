package com.tms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import com.tms.mapper.TaskMapper;
import com.tms.model.Project;
import com.tms.model.Task;
import com.tms.model.User;
import com.tms.repository.project.ProjectRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.impl.DropboxService;
import com.tms.service.impl.TaskServiceImpl;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private TaskMapper taskMapper;
    @InjectMocks
    private TaskServiceImpl taskService;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DropboxService dropboxService;

    @Test
    void save_withValidRequest_ShouldReturnDtoOfSavedTask() {
        // Given
        Long assigneeId = 1L;
        Long projectId = 1L;
        Task task = createTask();
        Task savedTask = new Task();
        savedTask.setId(task.getId());
        savedTask.setName(task.getName());
        savedTask.setDescription(task.getDescription());
        savedTask.setPriority(task.getPriority());
        savedTask.setStatus(task.getStatus());
        TaskDto expected = new TaskDto(
                savedTask.getId(),
                "Task",
                "Task desc",
                Task.TaskPriority.MEDIUM,
                Task.TaskStatus.IN_PROGRESS,
                LocalDate.of(2025, 6, 1),
                projectId,
                assigneeId
        );
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "Task",
                "Task desc",
                Task.TaskPriority.MEDIUM,
                Task.TaskStatus.IN_PROGRESS,
                LocalDate.of(2025, 6, 1),
                projectId,
                assigneeId
        );
        when(projectRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(new Project()));
        when(taskMapper.toModel(requestDto)).thenReturn(task);
        when(taskRepository.save(task)).thenReturn(savedTask);
        when(taskMapper.toDto(any(Task.class))).thenReturn(expected);
        when(userRepository.findById(assigneeId)).thenReturn(Optional.of(new User()));
        // When
        TaskDto actual = taskService.save(anyLong(), requestDto);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(taskMapper).toModel(requestDto);
        verify(taskRepository).save(task);
        verify(taskMapper).toDto(any(Task.class));
        verify(userRepository).findById(assigneeId);
    }

    @Test
    void getAllFromTask_withPageable_ShouldReturnAllTasksOfProject() {
        // Given
        Long userId = 1L;
        Long projectId = 1L;
        Long assignee1Id = 2L;
        Long assignee2Id = 3L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Task> tasks = List.of(new Task(), new Task());
        Page<Task> taskPage = new PageImpl<>(tasks);
        when(taskRepository.findAllByProjectIdAndUserHasAccess(userId, projectId, pageable))
                .thenReturn(taskPage);
        List<TaskDto> expected = List.of(
                new TaskDto(
                        1L,
                        "Task 1",
                        "Task 1 desc",
                        Task.TaskPriority.MEDIUM,
                        Task.TaskStatus.IN_PROGRESS,
                        LocalDate.of(2025, 6, 1),
                        projectId,
                        assignee1Id),
                new TaskDto(
                        2L,
                        "Task 2",
                        "Task 2 desc",
                        Task.TaskPriority.HIGH,
                        Task.TaskStatus.COMPLETED,
                        LocalDate.of(2025, 6, 1),
                        projectId,
                        assignee2Id)
        );
        when(taskMapper.toDto(any(Task.class)))
                .thenAnswer(invocation -> {
                    Task task = invocation.getArgument(0);
                    int index = tasks.indexOf(task);
                    return expected.get(index);
                });
        // When
        Page<TaskDto> actual = taskService.getAllFromProject(userId, projectId, pageable);
        // Then
        verify(taskRepository).findAllByProjectIdAndUserHasAccess(userId, projectId, pageable);
        verify(taskMapper, times(tasks.size())).toDto(any(Task.class));
        assertEquals(expected, actual.getContent());
    }

    @Test
    void getById_withValidTaskAndUserId_ShouldReturnValidTask() {
        // Given
        Task task = createTask();
        TaskDto expected = new TaskDto(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getPriority(),
                task.getStatus(),
                task.getDueDate(),
                task.getProject().getId(),
                task.getAssignee().getId()
        );
        when(taskRepository.findAccessibleTask(anyLong(), anyLong()))
                .thenReturn(Optional.of(task));
        when(taskMapper.toDto(any(Task.class))).thenReturn(expected);
        // When
        TaskDto actual = taskService.getById(anyLong(), anyLong());
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(taskRepository).findAccessibleTask(anyLong(), anyLong());
        verify(taskMapper).toDto(any(Task.class));
    }

    @Test
    void update_withValidRequest_ShouldReturnDtoOfUpdatedTask() {
        Long projectId = 1L;
        Long assigneeId = 2L;
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "Updated task",
                "Updated task description",
                Task.TaskPriority.HIGH,
                Task.TaskStatus.COMPLETED,
                LocalDate.of(2020, 6, 1),
                projectId,
                assigneeId
        );
        Task task = createTask();
        when(taskRepository.findTaskByIdAndProjectOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(task));
        taskService.update(anyLong(), anyLong(), requestDto);
        verify(taskMapper).updateTask(requestDto, task);
        verify(taskRepository).save(task);
        verify(taskMapper).toDto(task);
    }

    @Test
    void deleteById_withValidTaskAndUserId_ShouldDeleteTask() {
        // Given
        Task task = createTask();
        when(taskRepository.findTaskByIdAndProjectOwner(anyLong(), anyLong()))
                .thenReturn(Optional.of(task));
        doNothing().when(dropboxService).deleteFolder(anyString());
        doNothing().when(taskRepository).deleteById(anyLong());
        // When
        taskService.deleteById(anyLong(), anyLong());
        // Then
        verify(dropboxService).deleteFolder(anyString());
        verify(taskRepository).deleteById(anyLong());
    }

    private Task createTask() {
        Long taskId = 1L;
        Task task = new Task();
        task.setId(taskId);
        task.setName("Task");
        task.setDescription("Task desc");
        task.setPriority(Task.TaskPriority.MEDIUM);
        task.setStatus(Task.TaskStatus.IN_PROGRESS);
        task.setDueDate(LocalDate.of(2025, 12, 1));
        task.setProject(new Project());
        task.setAssignee(new User());
        return task;
    }
}
