package com.proman.issue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends JpaRepository<Issue, UUID>,
        org.springframework.data.jpa.repository.JpaSpecificationExecutor<Issue> {

    @Query("SELECT COALESCE(MAX(i.issueNumber), 0) FROM Issue i WHERE i.project.id = :projectId")
    int findMaxIssueNumber(UUID projectId);

    List<Issue> findByProjectIdOrderByCreatedAtDesc(UUID projectId);

    List<Issue> findByProjectIdAndStatusId(UUID projectId, UUID statusId);

    List<Issue> findBySprintId(UUID sprintId);

    @Query("SELECT i FROM Issue i WHERE i.project.id = :projectId " +
           "AND (i.createdAt < :cursorTime OR (i.createdAt = :cursorTime AND i.id < :cursorId)) " +
           "ORDER BY i.createdAt DESC, i.id DESC")
    List<Issue> findByProjectIdWithCursor(UUID projectId, Instant cursorTime, UUID cursorId,
                                          org.springframework.data.domain.Pageable pageable);

    @Query("SELECT i FROM Issue i WHERE i.project.id = :projectId ORDER BY i.createdAt DESC, i.id DESC")
    List<Issue> findByProjectIdFirstPage(UUID projectId, org.springframework.data.domain.Pageable pageable);

    @Query("SELECT SUM(i.storyPoints) FROM Issue i " +
           "WHERE i.sprint.id = :sprintId AND i.status.category = 'DONE'")
    Optional<Integer> sumCompletedStoryPoints(UUID sprintId);

    @Query("SELECT SUM(i.storyPoints) FROM Issue i WHERE i.sprint.id = :sprintId")
    Optional<Integer> sumTotalStoryPoints(UUID sprintId);

    List<Issue> findBySprintIdAndStatusCategoryNot(UUID sprintId, com.proman.workflow.StatusCategory category);
}
