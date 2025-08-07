package ru.gamehub.web.infrastructure.jpa.project.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberJpaEntity, ProjectMemberJpaId> {
    List<ProjectMemberJpaEntity> findAllByProjectId(UUID projectId);
    List<ProjectMemberJpaEntity> findAllByUserIdIn(List<UUID> userId);
}
