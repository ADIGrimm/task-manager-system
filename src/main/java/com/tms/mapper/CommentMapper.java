package com.tms.mapper;

import com.tms.config.MapperConfig;
import com.tms.dto.comment.CommentDto;
import com.tms.dto.comment.CreateCommentRequestDto;
import com.tms.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CommentMapper {
    @Mapping(source = "task.id", target = "taskId")
    @Mapping(source = "user.id", target = "userId")
    CommentDto toDto(Comment comment);

    Comment toModel(CreateCommentRequestDto requestDto);
}
