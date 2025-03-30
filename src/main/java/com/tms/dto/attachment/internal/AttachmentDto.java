package com.tms.dto.attachment.internal;

import java.time.LocalDateTime;

public record AttachmentDto(
        Long id,
        String fileId,
        String fileName,
        LocalDateTime uploadDate
) {
}
