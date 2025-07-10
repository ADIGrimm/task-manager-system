package com.tms.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.mapper.CommentMapper;
import com.tms.model.Comment;
import com.tms.model.Task;
import com.tms.model.User;
import com.tms.repository.comment.CommentRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.impl.CommentServiceImpl;
import java.time.LocalDateTime;
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
public class CommentServiceTest {
    @InjectMocks
    private CommentServiceImpl commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TaskRepository taskRepository;

    @Test
    void save_withValidRequest_ShouldReturnDtoOfSavedLabel() {
        // Given
        Comment comment = createComment();
        Long taskId = 1L;
        Comment savedComment = new Comment();
        savedComment.setId(comment.getId());
        savedComment.setTask(comment.getTask());
        savedComment.setUser(comment.getUser());
        savedComment.setText(comment.getText());
        savedComment.setTimestamp(comment.getTimestamp());
        CommentDto expected = new CommentDto(
                savedComment.getTask().getId(),
                savedComment.getUser().getId(),
                savedComment.getText(),
                savedComment.getTimestamp()
        );
        CreateCommentRequestDto requestDto = new CreateCommentRequestDto(
                taskId,
                "Comment text"
        );
        when(commentMapper.toModel(requestDto)).thenReturn(comment);
        when(taskRepository.findAccessibleTask(anyLong(), anyLong()))
                .thenReturn(Optional.of(comment.getTask()));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(comment.getUser()));
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.toDto(savedComment)).thenReturn(expected);
        // When
        CommentDto actual = commentService.save(anyLong(), requestDto);
        // Then
        assertNotNull(actual);
        assertEquals(expected, actual);
        verify(commentMapper).toModel(requestDto);
        verify(taskRepository).findAccessibleTask(anyLong(), anyLong());
        verify(userRepository).findById(anyLong());
        verify(commentRepository).save(comment);
        verify(commentMapper).toDto(savedComment);
    }

    @Test
    void getAll_withPageable_ShouldReturnAllCommentsOfTask() {
        // Given
        Long userId = 1L;
        Long taskId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = List.of(new Comment(), new Comment());
        Page<Comment> commentPage = new PageImpl<>(comments);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(new Task()));
        when(commentRepository.findAllByTaskIdAndAccessibleToUser(taskId, userId, pageable))
                .thenReturn(commentPage);
        List<CommentDto> commentDtos = List.of(
                new CommentDto(
                        taskId,
                        2L,
                        "Comment 1 text",
                        LocalDateTime.now()
                ),
                new CommentDto(
                        taskId,
                        3L,
                        "Comment 2 text",
                        LocalDateTime.now()
                )
        );
        when(commentMapper.toDto(any(Comment.class)))
                .thenAnswer(invocation -> {
                    Comment comment = invocation.getArgument(0);
                    int index = comments.indexOf(comment);
                    return commentDtos.get(index);
                });
        // When
        Page<CommentDto> actual = commentService.getAllCommentsOfTask(userId, taskId, pageable);
        // When
        verify(commentRepository).findAllByTaskIdAndAccessibleToUser(taskId, userId, pageable);
        verify(commentMapper, times(comments.size())).toDto(any(Comment.class));
        assertEquals(commentDtos, actual.getContent());
    }

    private Comment createComment() {
        Long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        comment.setTask(new Task());
        comment.setUser(new User());
        comment.setText("Comment text");
        comment.setTimestamp(LocalDateTime.now());
        return comment;
    }
}
