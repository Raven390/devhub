package ru.devhub.api.infrastructure.jpa.project.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import ru.devhub.api.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.devhub.api.infrastructure.jpa.reference.project.role.RoleJpaEntity;
import ru.devhub.api.infrastructure.jpa.user.UserJpaEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project_member", schema = "devhub",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "user_id"}))
public class ProjectMemberJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private ProjectJpaEntity project;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "status", length = 32)
    private ProjectMemberStatusJpa status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "devhub",
            name = "project_member_role",
            joinColumns = @JoinColumn(name = "project_member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleJpaEntity> roles = new java.util.ArrayList<>();

    @Column(name = "joined_at", nullable = false)
    private OffsetDateTime joinedAt;

    @Column(name = "left_at")
    private OffsetDateTime leftAt;

    public ProjectMemberJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public ProjectMemberStatusJpa getStatus() {
        return status;
    }

    public void setStatus(ProjectMemberStatusJpa status) {
        this.status = status;
    }

    public List<RoleJpaEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleJpaEntity> role) {
        this.roles = role;
    }

    public OffsetDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(OffsetDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public OffsetDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(OffsetDateTime leftAt) {
        this.leftAt = leftAt;
    }
}
