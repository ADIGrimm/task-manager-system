package com.tms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import com.tms.mapper.ProjectMapper;
import com.tms.model.Project;
import com.tms.model.User;
import com.tms.repository.project.ProjectRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.impl.DropboxService;
import com.tms.service.impl.ProjectServiceImpl;
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
public class ProjectServiceTest {
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ProjectMapper projectMapper;
    @InjectMocks
    private ProjectServiceImpl projectService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DropboxService dropboxService;

    @Test
    void save_withValidRequest_ShouldReturnDtoOfSavedProject() {
        // Given
        Project project = createProject();
        Project savedProject = new Project();
        savedProject.setId(project.getId());
        savedProject.setName(project.getName());
        savedProject.setDescription(project.getDescription());
        savedProject.setStartDate(project.getStartDate());
        savedProject.setEndDate(project.getEndDate());
        savedProject.setStatus(project.getStatus());
        savedProject.setUserId(project.getUserId());
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto(
                "Project",
                "Project desc",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 7, 1),
                Project.ProjectStatus.INITIATED
        );
        ProjectDto expected = new ProjectDto(
                1L,
                "Project",
                "Project desc",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 7, 1),
                Project.ProjectStatus.INITIATED
        );
        Long ownerId = 1L;
        when(projectMapper.toModel(requestDto)).thenReturn(project);
        when(projectRepository.save(project)).thenReturn(savedProject);
        when(projectMapper.toDto(any(Project.class))).thenReturn(expected);
        when(userRepository.findById(ownerId)).thenReturn(Optional.of(new User()));
        // When
        ProjectDto actual = projectService.save(ownerId, requestDto);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(projectMapper).toModel(requestDto);
        verify(projectRepository).save(project);
        verify(projectMapper).toDto(any(Project.class));
        verify(userRepository).findById(ownerId);
    }

    @Test
    void getAll_withPageable_ShouldReturnAllProjects() {
        // Given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Project> projects = List.of(new Project(), new Project());
        Page<Project> projectPage = new PageImpl<>(projects);
        when(projectRepository.findAllAccessibleToUser(userId, pageable))
                .thenReturn(projectPage);
        List<ProjectDto> expected = List.of(
                new ProjectDto(
                        1L,
                        "Project 1",
                        "Project 1 desc",
                        LocalDate.of(2025, 6, 1),
                        LocalDate.of(2025, 7, 1),
                        Project.ProjectStatus.INITIATED
                ),
                new ProjectDto(
                        2L,
                        "Project 2",
                        "Project 2 desc",
                        LocalDate.of(2025, 6, 2),
                        LocalDate.of(2025, 7, 2),
                        Project.ProjectStatus.INITIATED
                ));
        when(projectMapper.toDto(any(Project.class)))
                .thenAnswer(invocation -> {
                    Project project = invocation.getArgument(0);
                    int index = projects.indexOf(project);
                    return expected.get(index);
                });
        // When
        Page<ProjectDto> actual = projectService.getAll(userId, pageable);
        // Then
        verify(projectRepository).findAllAccessibleToUser(userId, pageable);
        verify(projectMapper, times(projects.size())).toDto(any(Project.class));
        assertEquals(expected, actual.getContent());
    }

    @Test
    void getById_withValidProjectAndUserId_ShouldReturnValidProject() {
        // Given
        Project project = createProject();
        ProjectDto expected = new ProjectDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getStartDate(),
                project.getEndDate(),
                project.getStatus()
        );
        when(projectRepository.findAccessibleProject(anyLong(), anyLong()))
                .thenReturn(Optional.of(project));
        when(projectMapper.toDto(any(Project.class))).thenReturn(expected);
        // When
        ProjectDto actual = projectService.getById(anyLong(), anyLong());
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(projectRepository).findAccessibleProject(anyLong(), anyLong());
        verify(projectMapper).toDto(any(Project.class));
    }

    @Test
    void update_withValidRequest_ShouldReturnDtoOfUpdatedProject() {
        // Given
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto(
                "Updated project",
                "Updated project description",
                LocalDate.of(2020, 6, 1),
                LocalDate.of(2020, 7, 1),
                Project.ProjectStatus.COMPLETED
        );
        Project project = createProject();
        when(projectRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(project));
        // When
        projectService.update(anyLong(), anyLong(), requestDto);
        // Then
        verify(projectMapper).updateProject(requestDto, project);
        verify(projectRepository).save(project);
        verify(projectMapper).toDto(project);
    }

    @Test
    void deleteById_withValidProjectAndUserId_ShouldDeleteProject() {
        // Given
        Project project = createProject();
        when(projectRepository.findByIdAndUserId(anyLong(), anyLong()))
                .thenReturn(Optional.of(project));
        doNothing().when(dropboxService).deleteFolder(anyString());
        doNothing().when(projectRepository).deleteById(anyLong());
        // When
        projectService.deleteById(anyLong(), anyLong());
        // Then
        verify(dropboxService).deleteFolder(anyString());
        verify(projectRepository).deleteById(anyLong());
    }

    private Project createProject() {
        Long projectId = 1L;
        Project project = new Project();
        project.setId(projectId);
        project.setName("Project");
        project.setDescription("Project desc");
        project.setStartDate(LocalDate.of(2025, 6, 1));
        project.setEndDate(LocalDate.of(2025, 7, 1));
        project.setStatus(Project.ProjectStatus.INITIATED);
        project.setUserId(new User());
        return project;
    }
}
