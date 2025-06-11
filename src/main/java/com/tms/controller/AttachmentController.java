package com.tms.controller;

import com.tms.dto.attachment.internal.AttachmentDto;
import com.tms.service.AttachmentService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController implements UserContextHelper {
    private final AttachmentService attachmentService;

    @Operation(summary = "Upload attachment",
            description = "Upload an attachment to a task")
    @PostMapping("/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDto uploadAttachment(
            Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @PathVariable Long taskId
    ) {
        return attachmentService.upload(getUserId(authentication), file, taskId);
    }

    @Operation(summary = "Retrieve attachments by task id",
            description = "Retrieve list of attachments by task id")
    @GetMapping("/{taskId}")
    public List<byte[]> getAttachmentsByTaskId(Authentication authentication,
                                               @PathVariable Long taskId) {
        return attachmentService.retrieveAttachmentsFromTask(getUserId(authentication), taskId);
    }
}
