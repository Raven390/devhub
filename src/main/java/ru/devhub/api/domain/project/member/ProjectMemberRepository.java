package ru.devhub.api.domain.project.member;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository {
    List<ProjectMember> findAllByProjectId(UUID projectId);
    List<ProjectMember> findAllByUserIds(List<UUID> userId);
    ProjectMember save(ProjectMember projectMember);
    List<ProjectMember> saveAll(Collection<ProjectMember> projectMemberList);
    void deleteAll(List<ProjectMember> projectMemberList);
}
