package com.proman.watcher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface IssueWatcherRepository extends JpaRepository<IssueWatcher, IssueWatcherId> {

    @Query("SELECT w FROM IssueWatcher w JOIN FETCH w.user WHERE w.issue.id = :issueId")
    List<IssueWatcher> findByIssueId(UUID issueId);

    boolean existsByIdIssueIdAndIdUserId(UUID issueId, UUID userId);
}
