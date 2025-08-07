package ru.gamehub.web.domain.project.member;

import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

public class ProjectMember {
    private final UUID projectId;
    private final User user;
    private final Role role;
    private final OffsetDateTime joinedAt;

    private ProjectMember(UUID projectId, User user, Role role, OffsetDateTime joinedAt) {
        this.projectId = projectId;
        this.user = user;
        this.role = role;
        this.joinedAt = joinedAt;
    }

    public static ProjectMember create(UUID projectId, User user, Role role, OffsetDateTime joinedAd) {
        return new ProjectMember(projectId, user, role, joinedAd);
    }

    public UUID getProjectId() {
        return projectId;
    }

    public User getUser() {
        return user;
    }

    public Role getRole() {
        return role;
    }

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }
}
