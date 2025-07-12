package com.tms.controller;

import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Comments", description = "Operations related to comments")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ROLE_USER')")
public class CommentController implements UserContextHelper {
    private final CommentService commentService;

    @Operation(summary = "Create comment",
            description = "Create comment")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(Authentication authentication,
                                    @Valid @RequestBody CreateCommentRequestDto requestDto) {
        return commentService.save(getUserId(authentication), requestDto);
    }

    @Operation(summary = "Get all comments of task",
            description = "Return list of comments of task as page")
    @GetMapping("/{taskId}")
    public Page<CommentDto> getAll(Authentication authentication, @PathVariable Long taskId,
                                   @ParameterObject @PageableDefault Pageable pageable) {
        return commentService.getAllCommentsOfTask(getUserId(authentication), taskId, pageable);
    }
}
