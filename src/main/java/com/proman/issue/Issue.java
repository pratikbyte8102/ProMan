package com.proman.issue;

import com.proman.auth.User;
import com.proman.common.BaseEntity;
import com.proman.project.Project;
import com.proman.sprint.Sprint;
import com.proman.workflow.WorkflowStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "issues")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Issue extends BaseEntity {
    @Column(name = "issue_number", nullable = false)
    private Integer issueNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType type;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "status_id", nullable = false)
    private WorkflowStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Issue parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Issue> children;

    @Column(name = "story_points")
    private Integer storyPoints;

    @Column(name = "labels", columnDefinition = "TEXT[]")
    private String[] labels;

    @Version
    @Column(nullable = false)
    private Integer version;
}
