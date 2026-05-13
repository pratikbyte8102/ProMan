package com.proman.workflow;

import com.proman.common.BaseEntity;
import com.proman.project.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_statuses", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "name"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowStatus extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCategory category;

    @Column(nullable = false)
    private Integer position;
}
