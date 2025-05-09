package com.tms.repository.comment;

import com.tms.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CommentRepository 
        extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    Page<Comment> findAllByTaskId(Long taskId, Pageable pageable);

}
