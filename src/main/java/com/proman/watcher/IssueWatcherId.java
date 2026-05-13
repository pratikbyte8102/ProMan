package com.proman.watcher;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class IssueWatcherId implements Serializable {
    @Column(name = "issue_id")
    private UUID issueId;
    @Column(name = "user_id")
    private UUID userId;
}
