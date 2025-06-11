package com.tms.repository.label;

import com.tms.model.Label;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LabelRepository
        extends JpaRepository<Label, Long>, JpaSpecificationExecutor<Label> {
    @Query("SELECT DISTINCT l FROM Label l JOIN Task t ON t.id = l.task.id "
            + "WHERE t.project.id = :projectId AND "
            + "(t.project.userId.id = :userId OR EXISTS "
            + "(SELECT t2 FROM Task t2 "
            + "WHERE t2.project.id = t.project.id AND t2.assignee.id = :userId))")
    Page<Label> findAllLabelsByProjectIdAndAccessibleToUser(
            @Param("projectId") Long projectId, @Param("userId") Long userId, Pageable pageable
    );

    @Query("SELECT l FROM Label l WHERE l.id = :labelId AND "
            + "(l.task.project.userId.id = :userId OR l.task.assignee.id = :userId)")
    Optional<Label> findAccessibleLabelById(
            @Param("labelId") Long labelId,
            @Param("userId") Long userId
    );
}
