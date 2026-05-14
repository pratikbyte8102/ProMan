package com.proman.comment;

import com.proman.auth.User;
import com.proman.comment.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/api/issues/{issueId}/comments")
    public ResponseEntity<CommentResponse> create(
            @PathVariable UUID issueId,
            @Valid @RequestBody CreateCommentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(commentService.create(issueId, request, user));
    }

    @GetMapping("/api/issues/{issueId}/comments")
    public ResponseEntity<List<CommentResponse>> list(@PathVariable UUID issueId) {
        return ResponseEntity.ok(commentService.listByIssue(issueId));
    }

    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<CommentResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCommentRequest request,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(commentService.update(id, request, user));
    }

    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal User user) {
        commentService.delete(id, user);
        return ResponseEntity.noContent().build();
    }
}
