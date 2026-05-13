package com.proman.customfield;

import com.proman.common.BaseEntity;
import com.proman.issue.Issue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_field_values", uniqueConstraints = @UniqueConstraint(columnNames = {"issue_id", "field_definition_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomFieldValue extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id", nullable = false)
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_definition_id", nullable = false)
    private CustomFieldDefinition fieldDefinition;

    @Column(columnDefinition = "TEXT")
    private String value;
}
