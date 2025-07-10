package com.tms.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.dto.task.CreateTaskRequestDto;
import com.tms.dto.task.TaskDto;
import com.tms.model.Task;
import com.tms.repository.task.TaskRepository;
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
public class TaskControllerTest implements UserNeeded {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DropboxService dropboxService;
    @Autowired
    private TaskRepository taskRepository;

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
                    new ClassPathResource("/database/tasks/setup-db-for-task-tests.sql")
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
                    new ClassPathResource("/database/tasks/add-five-tasks.sql")
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
                    new ClassPathResource("/database/tasks/clean-tasks-table.sql")
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
    void createTask_ValidRequestDto_Success() throws Exception {
        // Given
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "Task",
                "Task desc",
                Task.TaskPriority.MEDIUM,
                Task.TaskStatus.IN_PROGRESS,
                LocalDate.of(2025, 7, 1),
                1L,
                2L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        doNothing().when(dropboxService).createFolder(anyString());
        TaskDto expected = new TaskDto(
                1L,
                requestDto.name(),
                requestDto.description(),
                requestDto.priority(),
                requestDto.status(),
                requestDto.dueDate(),
                requestDto.projectId(),
                requestDto.assigneeId()
        );
        // When
        MvcResult result = mockMvc.perform(
                        post("/tasks")
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        TaskDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    void getAllFromProject_WithSomeProjects_ShouldReturnTwoTasks() throws Exception {
        // Given
        Long projectId = 1L;
        List<TaskDto> expected = List.of(
                new TaskDto(
                        1L,
                        "Task 1",
                        "Task 1 desc",
                        Task.TaskPriority.LOW,
                        Task.TaskStatus.NOT_STARTED,
                        LocalDate.of(2000, 6, 1),
                        1L,
                        2L
                ),
                new TaskDto(
                        2L,
                        "Task 2",
                        "Task 2 desc",
                        Task.TaskPriority.MEDIUM,
                        Task.TaskStatus.IN_PROGRESS,
                        LocalDate.of(2000, 6, 2),
                        1L,
                        3L
                )
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/tasks/project/" + projectId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        JsonNode contentNode = node.get("content");
        List<TaskDto> actual =
                Arrays.asList(objectMapper.treeToValue(contentNode, TaskDto[].class));
        // Then
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void getTaskById_returnsNecessaryTaskDtoForOwnedTasks() throws Exception {
        // Given
        Long necessaryTaskId = 2L;
        TaskDto expected = new TaskDto(
                necessaryTaskId,
                "Task 4",
                "Task 4 desc",
                Task.TaskPriority.MEDIUM,
                Task.TaskStatus.COMPLETED,
                LocalDate.of(2000, 6, 4),
                3L,
                2L
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/tasks/" + necessaryTaskId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    void updateTask_WithValidRequest_ShouldUpdateTask() throws Exception {
        // Given
        Long updatedTaskId = 4L;
        CreateTaskRequestDto requestDto = new CreateTaskRequestDto(
                "Task 4 Updated",
                "Task 4 desc Updated",
                Task.TaskPriority.LOW,
                Task.TaskStatus.COMPLETED,
                LocalDate.of(2025, 7, 1),
                3L,
                2L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        TaskDto expected = new TaskDto(
                updatedTaskId,
                requestDto.name(),
                requestDto.description(),
                requestDto.priority(),
                requestDto.status(),
                requestDto.dueDate(),
                requestDto.projectId(),
                requestDto.assigneeId()
        );
        // When
        MvcResult result = mockMvc.perform(
                        put("/tasks/" + updatedTaskId)
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        TaskDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), TaskDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        EqualsBuilder.reflectionEquals(expected, actual, "id");
    }

    @Test
    void deleteTask_WithValidRequest() throws Exception {
        // Given
        Long deletedTaskId = 5L;
        // When
        MvcResult result = mockMvc.perform(
                        delete("/tasks/" + deletedTaskId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        // Then
        assertTrue(taskRepository.findById(deletedTaskId).isEmpty());
    }
}
