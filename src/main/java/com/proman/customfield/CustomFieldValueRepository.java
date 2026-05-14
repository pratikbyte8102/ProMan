package com.proman.customfield;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomFieldValueRepository extends JpaRepository<CustomFieldValue, UUID> {
    List<CustomFieldValue> findByIssueId(UUID issueId);
    Optional<CustomFieldValue> findByIssueIdAndFieldDefinitionId(UUID issueId, UUID fieldDefinitionId);
    void deleteByIssueId(UUID issueId);
}
