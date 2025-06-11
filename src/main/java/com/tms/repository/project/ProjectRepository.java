package com.tms.repository.project;

import com.tms.model.Project;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjectRepository
        extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    @Query("SELECT DISTINCT p FROM Project p WHERE p.userId.id = :userId OR EXISTS "
            + "(SELECT t FROM Task t WHERE t.project.id = p.id AND t.assignee.id = :userId)")
    Page<Project> findAllAccessibleToUser(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT p FROM Project p WHERE p.id = :projectId AND (p.userId.id = :userId OR EXISTS "
            + "(SELECT t FROM Task t WHERE t.project.id = p.id AND t.assignee.id = :userId))")
    Optional<Project> findAccessibleProject(
            @Param("projectId") Long projectId,
            @Param("userId") Long userId
    );

    @Query("SELECT p FROM Project p WHERE p.id = :id AND p.userId.id = :userId")
    Optional<Project> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

}
