package com.proman.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.author WHERE c.issue.id = :issueId ORDER BY c.createdAt ASC")
    List<Comment> findByIssueIdOrderByCreatedAtAsc(UUID issueId);
}
