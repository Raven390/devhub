package ru.devhub.api.infrastructure.jpa.project.member.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.devhub.api.infrastructure.jpa.project.member.model.ProjectMemberJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMemberJpaEntity, UUID> {
    List<ProjectMemberJpaEntity> findAllByProjectId(UUID projectId);
    List<ProjectMemberJpaEntity> findAllByUserIdIn(List<UUID> userId);
}
