package com.proman.watcher;

import com.proman.auth.User;
import com.proman.issue.Issue;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "issue_watchers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class IssueWatcher {
    @EmbeddedId
    private IssueWatcherId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("issueId")
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
}
