package com.proman.customfield;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, UUID> {
    List<CustomFieldValue> findByIssueId(UUID issueId);
    void deleteByIssueId(UUID issueId);
}
