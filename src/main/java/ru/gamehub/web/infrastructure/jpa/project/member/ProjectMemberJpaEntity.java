package ru.gamehub.web.infrastructure.jpa.project.member;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import ru.gamehub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.gamehub.web.infrastructure.jpa.project.role.RoleJpaEntity;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaEntity;

import java.time.OffsetDateTime;

@Entity
@Table(name = "project_member", schema = "gamehub")
public class ProjectMemberJpaEntity {

    @EmbeddedId
    private ProjectMemberJpaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    @JoinColumn(name = "project_id")
    private ProjectJpaEntity project;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private RoleJpaEntity role;

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    public ProjectMemberJpaEntity() {
    }

    public ProjectMemberJpaId getId() {
        return id;
    }

    public void setId(ProjectMemberJpaId id) {
        this.id = id;
    }

    public ProjectJpaEntity getProject() {
        return project;
    }

    public void setProject(ProjectJpaEntity project) {
        this.project = project;
    }

    public UserJpaEntity getUser() {
        return user;
    }

    public void setUser(UserJpaEntity user) {
        this.user = user;
    }

    public RoleJpaEntity getRole() {
        return role;
    }

    public void setRole(RoleJpaEntity role) {
        this.role = role;
    }

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
