package com.tms.service;

import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentService {
    CommentDto save(Long userId, CreateCommentRequestDto requestDto);

    Page<CommentDto> getAllCommentsOfTask(Long taskId, Pageable pageable);
}
