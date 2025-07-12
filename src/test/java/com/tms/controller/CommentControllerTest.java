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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.repository.comment.CommentRepository;
import com.tms.service.impl.DropboxService;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
public class CommentControllerTest implements UserNeeded {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DropboxService dropboxService;
    @Autowired
    private CommentRepository commentRepository;

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
                    new ClassPathResource("/database/comments/setup-db-for-comment-tests.sql")
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
                    new ClassPathResource("/database/comments/add-two-comments.sql")
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
                    new ClassPathResource("/database/comments/clean-comments-table.sql")
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
    void createComment_ValidRequest_Success() throws Exception {
        // Given
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto(
                1L,
                "Comment desc"
        );
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        doNothing().when(dropboxService).createFolder(anyString());
        CommentDto expected = new CommentDto(
                1L,
                1L,
                requestDto.text(),
                LocalDateTime.now()
        );
        // When
        MvcResult result = mockMvc.perform(
                        post("/comments")
                                .with(authentication(createAuthToken()))
                                .content(jsonRequest)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn();
        CommentDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), CommentDto.class
        );
        // Then
        assertNotNull(actual);
        assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "userId", "timestamp"));
    }

    @Test
    void createComment_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        CreateCommentRequestDto dto = new CreateCommentRequestDto(
                0L,
                ""
        );

        mockMvc.perform(post("/comments")
                        .with(authentication(createAuthToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(2))
                .andExpect(jsonPath("$.errors", hasItem(containsString("taskId"))))
                .andExpect(jsonPath("$.errors", hasItem(containsString("text"))));
    }

    @Test
    void getAll_WithTwoCommentsFromNotSingleUser_ShouldReturnTwoComments() throws Exception {
        // Given
        Long taskId = 1L;
        List<CommentDto> expected = List.of(
                new CommentDto(1L, 1L, "Comment 1 text", LocalDateTime.of(2000, 6, 1, 1, 1, 1)),
                new CommentDto(1L, 2L, "Comment 2 text", LocalDateTime.of(2000, 6, 2, 2, 2, 2))
        );
        // When
        MvcResult result = mockMvc.perform(
                        get("/comments/" + taskId)
                                .with(authentication(createAuthToken()))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();
        String json = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(json);
        JsonNode contentNode = node.get("content");
        List<CommentDto> actual = Arrays.asList(
                objectMapper.treeToValue(contentNode, CommentDto[].class)
        );
        // Then
        assertEquals(2, actual.size());
        assertEquals(expected, actual);
    }
}
