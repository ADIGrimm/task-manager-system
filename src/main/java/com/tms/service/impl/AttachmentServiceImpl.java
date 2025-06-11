package com.tms.service.impl;

import com.tms.dto.attachment.external.UploadResponseDataDto;
import com.tms.dto.attachment.internal.AttachmentDto;
import com.tms.exception.EntityNotFoundException;
import com.tms.mapper.AttachmentMapper;
import com.tms.model.Attachment;
import com.tms.model.Task;
import com.tms.repository.attachment.AttachmentRepository;
import com.tms.repository.task.TaskRepository;
import com.tms.service.AttachmentService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final AttachmentRepository attachmentRepository;
    private final DropboxService dropboxService;
    private final AttachmentMapper attachmentMapper;
    private final TaskRepository taskRepository;

    @Override
    public AttachmentDto upload(Long userId, MultipartFile file, Long taskId) {
        Task task = taskRepository.findTaskByIdAndUser(taskId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find task by id " + taskId));
        String path = "projects/" + task.getProject().getId() + "/tasks/" + taskId;
        UploadResponseDataDto dropboxResponse = dropboxService.uploadFile(file, path);
        Attachment attachment = attachmentMapper.toModel(dropboxResponse);
        attachment.setTask(taskRepository.findById(taskId).orElseThrow(()
                -> new EntityNotFoundException("Can't find task by id " + taskId)));
        attachment.setUploadDate(LocalDateTime.now());
        attachment.setFilePath(path + "/" + file.getOriginalFilename());
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public List<byte[]> retrieveAttachmentsFromTask(Long userId, Long taskId) {
        if (taskRepository.findTaskByIdAndOwner(taskId, userId).isPresent()) {
            List<Attachment> attachments = attachmentRepository.findAllByTaskId(taskId);
            List<byte[]> files = new ArrayList<>();
            for (Attachment attachment : attachments) {
                String dropboxFileId = attachment.getFilePath();
                files.add(dropboxService.downloadFile(dropboxFileId));
            }
            return files;
        } else {
            throw new EntityNotFoundException("Can't find task by id " + taskId);
        }
    }
}
