package com.proman.workflow;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowStatusRepository extends JpaRepository<WorkflowStatus, UUID> {
    List<WorkflowStatus> findByProjectIdOrderByPositionAsc(UUID projectId);
    Optional<WorkflowStatus> findFirstByProjectIdAndCategoryOrderByPositionAsc(UUID projectId, StatusCategory category);
    int countByProjectId(UUID projectId);
}
