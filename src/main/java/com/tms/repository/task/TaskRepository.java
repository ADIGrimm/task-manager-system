package com.tms.repository.task;

import com.tms.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository
        extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    @Query("""
    SELECT t FROM Task t
    WHERE t.project.id = :projectId
    AND (
        t.project.userId.id = :userId
        OR EXISTS (
            SELECT 1 FROM Task t2
            WHERE t2.project.id = :projectId
            AND t2.assignee.id = :userId
        )
    )
    """)
    Page<Task> findAllByProjectIdAndUserHasAccess(
            @Param("userId") Long userId,
            @Param("projectId") Long projectId,
            Pageable pageable
    );


    @Query("SELECT t FROM Task t WHERE t.id = :taskId "
            + "AND (t.project.userId.id = :userId OR t.assignee.id = :userId)")
    Optional<Task> findAccessibleTask(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.project.userId.id = :userId")
    Optional<Task> findTaskByIdAndProjectOwner(
            @Param("taskId") Long taskId,
            @Param("userId") Long userId
    );

    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND " +
            "(t.assignee.id = :userId OR t.project.userId.id = :userId)")
    Optional<Task> findTaskByIdAndUser(@Param("taskId") Long taskId, @Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.id = :taskId AND t.project.userId.id = :userId")
    Optional<Task> findTaskByIdAndOwner(
            @Param("taskId") Long taskId,
            @Param("userId") Long userId
    );
}
