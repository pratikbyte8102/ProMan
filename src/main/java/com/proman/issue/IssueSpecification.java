package com.proman.issue;

import com.proman.workflow.StatusCategory;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IssueSpecification {

    public static Specification<Issue> withFilters(
            UUID projectId,
            UUID statusId,
            UUID assigneeId,
            UUID sprintId,
            IssueType type,
            Priority priority,
            StatusCategory statusCategory) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(root.get("project").get("id"), projectId));

            if (statusId != null) {
                predicates.add(cb.equal(root.get("status").get("id"), statusId));
            }
            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assignee").get("id"), assigneeId));
            }
            if (sprintId != null) {
                predicates.add(cb.equal(root.get("sprint").get("id"), sprintId));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("type"), type));
            }
            if (priority != null) {
                predicates.add(cb.equal(root.get("priority"), priority));
            }
            if (statusCategory != null) {
                predicates.add(cb.equal(root.get("status").get("category"), statusCategory));
            }

            query.orderBy(cb.desc(root.get("createdAt")), cb.desc(root.get("id")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
