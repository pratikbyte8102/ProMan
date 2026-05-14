package com.proman.comment;

import com.proman.auth.AuthService;
import com.proman.auth.User;
import com.proman.comment.dto.*;
import com.proman.common.exception.BusinessRuleException;
import com.proman.common.exception.ResourceNotFoundException;
import com.proman.issue.Issue;
import com.proman.issue.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final IssueService issueService;

    @Transactional
    public CommentResponse create(UUID issueId, CreateCommentRequest request, User author) {
        Issue issue = issueService.findIssue(issueId);

        Comment comment = Comment.builder()
            .issue(issue)
            .author(author)
            .body(request.body())
            .mentions(request.mentions() != null
                ? request.mentions().toArray(new UUID[0]) : null)
            .build();

        if (request.parentCommentId() != null) {
            Comment parent = commentRepository.findById(request.parentCommentId())
                .orElseThrow(() -> new ResourceNotFoundException("Comment", request.parentCommentId()));
            if (!parent.getIssue().getId().equals(issueId)) {
                throw new BusinessRuleException("Parent comment does not belong to this issue");
            }
            comment.setParentComment(parent);
        }

        comment = commentRepository.save(comment);
        return toResponse(comment);
    }

    public List<CommentResponse> listByIssue(UUID issueId) {
        return commentRepository.findByIssueIdOrderByCreatedAtAsc(issueId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public CommentResponse update(UUID commentId, UpdateCommentRequest request, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BusinessRuleException("Only the author can edit their comment");
        }

        comment.setBody(request.body());
        return toResponse(commentRepository.save(comment));
    }

    @Transactional
    public void delete(UUID commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new ResourceNotFoundException("Comment", commentId));

        if (!comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new BusinessRuleException("Only the author can delete their comment");
        }

        commentRepository.deleteById(commentId);
    }

    private CommentResponse toResponse(Comment c) {
        return new CommentResponse(
            c.getId(),
            c.getIssue().getId(),
            AuthService.toUserResponse(c.getAuthor()),
            c.getParentComment() != null ? c.getParentComment().getId() : null,
            c.getBody(),
            c.getMentions() != null ? List.of(c.getMentions()) : List.of(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }
}
