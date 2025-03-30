package com.tms.service;

import com.tms.dto.attachment.internal.AttachmentDto;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    AttachmentDto upload(MultipartFile file, Long taskId);

    List<byte[]> retrieveAttachmentsFromTask(Long taskId);
}
