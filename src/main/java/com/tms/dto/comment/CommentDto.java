package com.tms.dto.comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long taskId,
        Long userId,
        String text,
        LocalDateTime timestamp
) {
}
