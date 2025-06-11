package com.tms.service.impl;

import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.CommentMapper;
import com.tms.model.Comment;
import com.tms.model.Task;
import com.tms.repository.comment.CommentRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.repository.user.UserRepository;
import com.tms.service.CommentService;
import com.tms.service.NotificationService;
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
    private final NotificationService notificationService;

    @Override
    public CommentDto save(Long userId, CreateCommentRequestDto requestDto) {
        Comment comment = commentMapper.toModel(requestDto);
        Task task = taskRepository.findAccessibleTask(requestDto.taskId(), userId).orElseThrow(
                () -> new EntityNotFoundException("Task not found with id: " + requestDto.taskId()
                ));
        comment.setTask(task);
        comment.setUser(userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id: " + userId)));
        comment.setTimestamp(LocalDateTime.now());
        notificationService.sendCommentNotification(task.getAssignee(), task, requestDto.text());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public Page<CommentDto> getAllCommentsOfTask(Long userId, Long taskId, Pageable pageable) {
        if (taskRepository.findById(taskId).isPresent()) {
            return commentRepository.findAllByTaskIdAndAccessibleToUser(taskId, userId, pageable)
                    .map(commentMapper::toDto);
        } else {
            throw new EntityNotFoundException("Task not found with id: " + taskId);
        }
    }
}
