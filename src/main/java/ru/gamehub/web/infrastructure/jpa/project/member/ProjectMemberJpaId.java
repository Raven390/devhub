package ru.gamehub.web.infrastructure.jpa.project.member;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class ProjectMemberJpaId implements Serializable {
    private UUID projectId;
    private UUID userId;

    public ProjectMemberJpaId(UUID projectId, UUID userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public ProjectMemberJpaId() {

    }

    public UUID getProjectId() {
        return projectId;
    }

    public void setProjectId(UUID projectId) {
        this.projectId = projectId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMemberJpaId that)) return false;
        return Objects.equals(getProjectId(), that.getProjectId()) && Objects.equals(getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getUserId());
    }
}

