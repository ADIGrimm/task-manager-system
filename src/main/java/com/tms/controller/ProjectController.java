package com.tms.controller;

import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import com.tms.service.ProjectService;
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
@RequestMapping("/projects")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class ProjectController implements UserContextHelper {
    private final ProjectService projectService;

    @Operation(summary = "Create project",
            description = "Create project")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto createProject(Authentication authentication,
                                    @Valid @RequestBody CreateProjectRequestDto projectDto) {
        return projectService.save(getUserId(authentication), projectDto);
    }

    @Operation(summary = "Get all projects",
            description = "Return list of projects as page")
    @GetMapping
    public Page<ProjectDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return projectService.getAll(pageable);
    }

    @Operation(summary = "Get project by id",
            description = "Return project with specified id")
    @GetMapping("/{id}")
    public ProjectDto getProjectById(@PathVariable Long id) {
        return projectService.getById(id);
    }

    @Operation(summary = "Update project information",
            description = "Update project information")
    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable Long id,
                              @Valid @RequestBody CreateProjectRequestDto projectDto) {
        return projectService.update(id, projectDto);
    }

    @Operation(summary = "Delete project by id",
            description = "Delete project by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteById(id);
    }
}
