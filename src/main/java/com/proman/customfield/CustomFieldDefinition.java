package com.proman.customfield;

import com.proman.common.BaseEntity;
import com.proman.project.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "custom_field_definitions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CustomFieldDefinition extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    @Column(columnDefinition = "JSONB")
    private String options;

    @Column(nullable = false)
    private Boolean required;
}
