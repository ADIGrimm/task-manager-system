package com.tms.repository.attachment;

import com.tms.model.Attachment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttachmentRepository
        extends JpaRepository<Attachment, Long>, JpaSpecificationExecutor<Attachment> {
    List<Attachment> findAllByTaskId(Long taskId);
}
