package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;

import java.util.List;
import java.util.UUID;

public class InMemoryProjectMemoryRepository extends BaseInMemoryRepository<ProjectMember, UUID> implements ProjectMemberRepository {
    @Override
    protected UUID getId(ProjectMember entity) {
        return entity.getUser().getId();
    }

    @Override
    public List<ProjectMember> findAllByProjectId(UUID projectId) {
        return this.store.values().stream().filter(obj -> obj.getProjectId().equals(projectId)).toList();
    }

    @Override
    public List<ProjectMember> findAllByUserIds(List<UUID> userId) {
        return this.store.values().stream().filter(obj -> userId.contains(obj.getUser().getId())).toList();
    }
}
