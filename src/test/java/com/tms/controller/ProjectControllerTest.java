package com.tms.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.dto.project.CreateProjectRequestDto;
import com.tms.dto.project.ProjectDto;
import com.tms.model.Project;
import com.tms.repository.project.ProjectRepository;
import com.tms.service.impl.DropboxService;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest implements UserNeeded {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DropboxService dropboxService;
    @Autowired
    private ProjectRepository projectRepository;

    @BeforeAll
    static void beforeAll(
            @Autowired DataSource dataSource,
            @Autowired WebApplicationContext applicationContext
    ) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("/database/projects/setup-db-for-project-tests.sql")
            );
        }
    }

    @BeforeEach
    void beforeEach(
            @Autowired DataSource dataSource
    ) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("/database/projects/add-three-projects.sql")
            );
        }
    }

    @AfterEach
    void afterEach(
            @Autowired DataSource dataSource
    ) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("/database/projects/clean-projects-table.sql")
            );
        }
    }

    @SneakyThrows
    private static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(
                    connection,
                    new ClassPathResource("/database/delete-all.sql")
            );
        }
    }

    @Test
    void createProject_ValidRequest_Success() throws Exception {
        // Given
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto(
                "Project",
                "Project desc",
                LocalDate.of(2025, 6, 1),
                LocalDate.of(2025, 7, 1),
                Project.ProjectStatus.IN_PROGRESS
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        doNothing().when(dropboxService).createFolder(anyString());
        ProjectDto expected = new ProjectDto(
                4L,
                requestDto.name(),
                requestDto.description(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.status()
        );
        // When
        MvcResult result = mockMvc.perform(
                        post("/projects")
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        ProjectDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    void createProject_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateProjectRequestDto dto = new CreateProjectRequestDto(
                "",
                "desc",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                null
        );

        mockMvc.perform(post("/projects")
                        .with(authentication(createAuthToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors", hasItem(containsString("name"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("status"))));
    }

    @Test
    void getAll_WithTwoOwners_ShouldReturnTwoProjects() throws Exception {
        // Given
        List<ProjectDto> expected = List.of(
                new ProjectDto(
                        1L,
                        "Project 1",
                        "Project 1 desc",
                        LocalDate.of(2000, 6, 1),
                        LocalDate.of(2002, 6, 1),
                        Project.ProjectStatus.INITIATED),
                new ProjectDto(
                        3L,
                        "Project 3",
                        "Project 3 desc",
                        LocalDate.of(2000, 6, 3),
                        LocalDate.of(2002, 6, 3),
                        Project.ProjectStatus.COMPLETED)
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/projects")
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        JsonNode contentNode = node.get("content");
        List<ProjectDto> actual =
                Arrays.asList(objectMapper.treeToValue(contentNode, ProjectDto[].class));
        // Then
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void getProjectById_returnsNecessaryProjectDtoForOwnedProjects() throws Exception {
        // Given
        Long necessaryProjectId = 3L;
        ProjectDto expected = new ProjectDto(
                necessaryProjectId,
                "Project 3",
                "Project 3 desc",
                LocalDate.of(2000, 6, 3),
                LocalDate.of(2002, 6, 3),
                Project.ProjectStatus.COMPLETED
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/projects/" + necessaryProjectId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    void getProjectById_withNotExistedId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(get("/projects/" + 5)
                        .with(authentication(createAuthToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.message")
                        .value(containsString("Can't find project by id 5 and user id 1")))
                .andExpect(jsonPath("$.error").value(containsString("Entity not found")));
    }

    @Test
    void updateProject_WithValidRequest_ShouldUpdateProject() throws Exception {
        // Given
        Long updatedProjectId = 1L;
        CreateProjectRequestDto requestDto = new CreateProjectRequestDto(
                "Project 1 Updated",
                "Project 1 desc Updated",
                LocalDate.of(2025, 6, 10),
                LocalDate.of(2025, 7, 10),
                Project.ProjectStatus.IN_PROGRESS
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        ProjectDto expected = new ProjectDto(
                updatedProjectId,
                requestDto.name(),
                requestDto.description(),
                requestDto.startDate(),
                requestDto.endDate(),
                requestDto.status()
        );
        // When
        MvcResult result = mockMvc.perform(
                        put("/projects/" + updatedProjectId)
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        ProjectDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), ProjectDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    void deleteProject_WithValidRequest() throws Exception {
        // Given
        Long deletedProjectId = 3L;
        // When
        MvcResult result = mockMvc.perform(
                        delete("/projects/" + deletedProjectId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        // Then
        assertTrue(projectRepository.findById(deletedProjectId).isEmpty());
    }

    @Test
    void deleteProjectById_withNotExistedId_ShouldReturnNotFound() {

    }
}
