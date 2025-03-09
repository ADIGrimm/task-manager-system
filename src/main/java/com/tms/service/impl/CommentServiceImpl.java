package com.tms.service.impl;

import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.CommentMapper;
import com.tms.model.Comment;
import com.tms.repository.comment.CommentRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.CommentService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Override
    public CommentDto save(Long userId, CreateCommentRequestDto requestDto) {
        Comment comment = commentMapper.toModel(requestDto);
        comment.setTask(taskRepository.findById(requestDto.taskId()).orElseThrow(
                () -> new EntityNotFoundException("Task not found with id: " + requestDto.taskId()
                )));
        comment.setUser(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)));
        comment.setTimestamp(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public Page<CommentDto> getAllCommentsOfTask(Long taskId, Pageable pageable) {
        taskRepository.findById(taskId).orElseThrow(
                () -> new EntityNotFoundException("Task not found with id: " + taskId)
        );
        return commentRepository.findAllByTaskId(taskId, pageable).map(commentMapper::toDto);
    }
}
