package com.proman.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, UUID> {
    List<WorkflowTransition> findByProjectId(UUID projectId);
    List<WorkflowTransition> findByProjectIdAndFromStatusId(UUID projectId, UUID fromStatusId);
    Optional<WorkflowTransition> findByProjectIdAndFromStatusIdAndToStatusId(
        UUID projectId, UUID fromStatusId, UUID toStatusId);
}
