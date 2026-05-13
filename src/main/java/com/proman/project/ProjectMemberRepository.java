package com.proman.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {
    List<ProjectMember> findByIdProjectId(UUID projectId);
    boolean existsByIdProjectIdAndIdUserId(UUID projectId, UUID userId);
}
