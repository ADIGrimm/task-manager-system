package com.tms.repository.comment;

import com.tms.model.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository 
        extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {
    @Query("SELECT c FROM Comment c WHERE c.task.id = :taskId AND "
            + "(c.task.project.userId.id = :userId OR EXISTS "
            + "(SELECT t FROM Task t WHERE t.project.id = c.task.project.id AND t.assignee.id = :userId))")
    Page<Comment> findAllByTaskIdAndAccessibleToUser(
            @Param("taskId") Long taskId,
            @Param("userId") Long userId,
            Pageable pageable
    );
}
