package com.proman.watcher;

import com.proman.auth.AuthService;
import com.proman.auth.User;
import com.proman.auth.dto.UserResponse;
import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WatcherController {

    private final IssueWatcherRepository watcherRepository;
    private final IssueService issueService;

    @PostMapping("/api/issues/{issueId}/watch")
    public ResponseEntity<Void> watch(
            @PathVariable UUID issueId,
            @AuthenticationPrincipal User user) {
        var issue = issueService.findIssue(issueId);

        if (watcherRepository.existsByIdIssueIdAndIdUserId(issueId, user.getId())) {
            throw new BusinessRuleException("Already watching this issue");
        }

        IssueWatcher watcher = IssueWatcher.builder()
            .id(new IssueWatcherId(issueId, user.getId()))
            .issue(issue)
            .user(user)
            .build();
        watcherRepository.save(watcher);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/issues/{issueId}/watch")
    public ResponseEntity<Void> unwatch(
            @PathVariable UUID issueId,
            @AuthenticationPrincipal User user) {
        IssueWatcherId id = new IssueWatcherId(issueId, user.getId());
        if (!watcherRepository.existsById(id)) {
            throw new ResourceNotFoundException("IssueWatcher", id);
        }
        watcherRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/issues/{issueId}/watchers")
    public ResponseEntity<List<UserResponse>> getWatchers(@PathVariable UUID issueId) {
        List<UserResponse> watchers = watcherRepository.findByIssueId(issueId).stream()
            .map(w -> AuthService.toUserResponse(w.getUser()))
            .toList();
        return ResponseEntity.ok(watchers);
    }
}
