package ru.gamehub.web.domain.project.member;

import java.util.List;
import java.util.UUID;

public interface ProjectMemberRepository {
    List<ProjectMember> findAllByProjectId(UUID projectId);
    List<ProjectMember> findAllByUserIds(List<UUID> userId);
    ProjectMember save(ProjectMember projectMember);
}
