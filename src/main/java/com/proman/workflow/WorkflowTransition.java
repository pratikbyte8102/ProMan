package com.proman.workflow;

import com.proman.common.BaseEntity;
import com.proman.project.Project;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "workflow_transitions", uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "from_status_id", "to_status_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkflowTransition extends BaseEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_status_id", nullable = false)
    private WorkflowStatus fromStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_status_id", nullable = false)
    private WorkflowStatus toStatus;

    @Column(name = "auto_assign_role", length = 50)
    private String autoAssignRole;

    @Column(name = "required_fields", columnDefinition = "TEXT[]")
    private String[] requiredFields;
}
