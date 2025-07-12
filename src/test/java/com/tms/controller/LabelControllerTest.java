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
import com.tms.dto.label.CreateLabelRequestDto;
import com.tms.dto.label.LabelDto;
import com.tms.repository.label.LabelRepository;
import com.tms.service.impl.DropboxService;
import java.sql.Connection;
import java.sql.SQLException;
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
public class LabelControllerTest implements UserNeeded {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DropboxService dropboxService;
    @Autowired
    private LabelRepository labelRepository;

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
                    new ClassPathResource("/database/labels/setup-db-for-label-tests.sql")
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
                    new ClassPathResource("/database/labels/add-four-labels.sql")
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
                    new ClassPathResource("/database/labels/clean-labels-table.sql")
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
    void createLabel_ValidRequest_Success() throws Exception {
        // Given
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto(
                "Label",
                "#ffffff",
                1L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        doNothing().when(dropboxService).createFolder(anyString());
        LabelDto expected = new LabelDto(
                1L,
                requestDto.name(),
                requestDto.color(),
                requestDto.taskId()
        );
        // When
        MvcResult result = mockMvc.perform(
                        post("/labels")
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        LabelDto actual = objectMapper.readValue(result.getResponse()
                .getContentAsString(), LabelDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    void createLabel_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateLabelRequestDto dto = new CreateLabelRequestDto(
                "",
                "",
                0L
        );

        mockMvc.perform(post("/labels")
                        .with(authentication(createAuthToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(4))
                .andExpect(jsonPath("$.errors", hasItem(containsString("name"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("color"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("taskId"))));
    }

    @Test
    void getAll_WithTwoOwners_ShouldReturnTwoLabels() throws Exception {
        // Given
        Long projectId = 1L;
        List<LabelDto> expected = List.of(
                new LabelDto(1L, "Label 1", "#ffffff", 1L),
                new LabelDto(2L, "Label 2", "#ffffff", 1L),
                new LabelDto(3L, "Label 3", "#ffffff", 2L)
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/labels/" + projectId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        JsonNode contentNode = node.get("content");
        // Then
        List<LabelDto> actual =
                Arrays.asList(objectMapper.treeToValue(contentNode, LabelDto[].class));
        assertEquals(3, actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void updateLabel_WithValidRequest_ShouldUpdateLabel() throws Exception {
        // Given
        Long updatedLabelId = 3L;
        CreateLabelRequestDto requestDto = new CreateLabelRequestDto(
                "Label 3 Updated",
                "#ffffff",
                3L
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        LabelDto expected = new LabelDto(
                updatedLabelId,
                requestDto.name(),
                requestDto.color(),
                requestDto.taskId()
        );
        // When
        MvcResult result = mockMvc.perform(
                        put("/labels/" + updatedLabelId)
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        LabelDto actual =
                objectMapper.readValue(result.getResponse().getContentAsString(), LabelDto.class);
        // Then
        assertNotNull(actual);
        assertNotNull(actual.id());
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "id"));
    }

    @Test
    void deleteLabel_WithValidRequest() throws Exception {
        // Given
        Long deletedLabelId = 4L;
        // When
        MvcResult result = mockMvc.perform(
                        delete("/labels/" + deletedLabelId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andReturn();
        // Then
        assertTrue(labelRepository.findById(deletedLabelId).isEmpty());
    }

    @Test
    void deleteTaskById_withNotExistedId_ShouldReturnNotFound() throws Exception {
        mockMvc.perform(delete("/labels/" + 5)
                        .with(authentication(createAuthToken()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.timestamp").isString())
                .andExpect(jsonPath("$.message").value(containsString("Can't find label by id: 5")))
                .andExpect(jsonPath("$.error").value(containsString("Entity not found")));
    }
}
