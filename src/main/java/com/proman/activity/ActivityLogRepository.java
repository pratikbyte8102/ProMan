package com.proman.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID> {

    @Query("SELECT a FROM ActivityLog a JOIN FETCH a.user WHERE a.issue.id = :issueId ORDER BY a.createdAt DESC")
    List<ActivityLog> findByIssueIdOrderByCreatedAtDesc(UUID issueId);

    @Query("SELECT a FROM ActivityLog a JOIN FETCH a.user WHERE a.project.id = :projectId ORDER BY a.createdAt DESC")
    List<ActivityLog> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
