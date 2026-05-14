package com.proman.workflow;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkflowTransitionRepository extends JpaRepository<WorkflowTransition, UUID> {

    @Query("SELECT t FROM WorkflowTransition t JOIN FETCH t.fromStatus JOIN FETCH t.toStatus WHERE t.project.id = :projectId")
    List<WorkflowTransition> findByProjectId(UUID projectId);

    @Query("SELECT t FROM WorkflowTransition t JOIN FETCH t.fromStatus JOIN FETCH t.toStatus WHERE t.project.id = :projectId AND t.fromStatus.id = :fromStatusId")
    List<WorkflowTransition> findByProjectIdAndFromStatusId(UUID projectId, UUID fromStatusId);

    Optional<WorkflowTransition> findByProjectIdAndFromStatusIdAndToStatusId(
        UUID projectId, UUID fromStatusId, UUID toStatusId);
}
