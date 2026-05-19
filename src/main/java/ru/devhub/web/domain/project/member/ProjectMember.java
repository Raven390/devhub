package ru.devhub.web.domain.project.member;

import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.user.User;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class ProjectMember {
    private final UUID id;
    private final UUID projectId;
    private final User user;
    private final List<Role> roles;
    private final ProjectMemberStatus status;
    private final OffsetDateTime joinedAt;
    private final OffsetDateTime leftAt;

    private ProjectMember(UUID id,
                          UUID projectId,
                          User user,
                          List<Role> roles,
                          ProjectMemberStatus status,
                          OffsetDateTime joinedAt,
                          OffsetDateTime leftAt) {
        this.id = id;
        this.projectId = Objects.requireNonNull(projectId, "projectId is required");
        this.user = Objects.requireNonNull(user, "user is required");
        this.roles = normalizeRoles(roles);
        this.status = Objects.requireNonNull(status, "status is required");
        this.joinedAt = Objects.requireNonNullElseGet(joinedAt, OffsetDateTime::now);
        this.leftAt = leftAt;
    }

    public static ProjectMember create(UUID id, UUID projectId, User user, List<Role> roles,
                                       ProjectMemberStatus status, OffsetDateTime joinedAt, OffsetDateTime leftAt) {
        return new ProjectMember(id != null ? id : UUID.randomUUID(), projectId, user, roles, status, joinedAt, leftAt);
    }

    public static ProjectMember create(UUID projectId, User user, List<Role> roles, ProjectMemberStatus status) {
        return new ProjectMember(UUID.randomUUID(), projectId, user, roles, status, OffsetDateTime.now(), null);
    }

    // -------- ИММУТАБЕЛЬНЫЕ "СЕТТЕРЫ" --------

    /** Вернёт НОВОГО участника с заменённым набором ролей (дедуп по id, порядок сохранён). */
    public ProjectMember withRoles(List<Role> newRoles) {
        return new ProjectMember(
                this.id,
                this.projectId,
                this.user,
                newRoles,
                this.status,
                this.joinedAt,
                this.leftAt
        );
    }

    /**
     * Вернёт НОВОГО участника с новым статусом.
     * Бизнес-гард: владельца (OWNER) нельзя понизить.
     */
    public ProjectMember withStatus(ProjectMemberStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus is required");
        if (this.status == ProjectMemberStatus.OWNER && newStatus != ProjectMemberStatus.OWNER) {
            throw new IllegalStateException("Owner status cannot be changed");
        }
        return new ProjectMember(
                this.id,
                this.projectId,
                this.user,
                this.roles,
                newStatus,
                this.joinedAt,
                this.leftAt
        );
    }

    /** Пометить выход участника (если понадобится). */
    public ProjectMember withLeftAt(OffsetDateTime when) {
        return new ProjectMember(
                this.id,
                this.projectId,
                this.user,
                this.roles,
                this.status,
                this.joinedAt,
                when != null ? when : OffsetDateTime.now()
        );
    }

    // -------- ГЕТТЕРЫ --------

    public UUID getId() { return id; }
    public UUID getProjectId() { return projectId; }
    public User getUser() { return user; }
    public List<Role> getRoles() { return roles; } // уже иммутабельный
    public ProjectMemberStatus getStatus() { return status; }
    public OffsetDateTime getJoinedAt() { return joinedAt; }
    public OffsetDateTime getLeftAt() { return leftAt; }

    // -------- ВСПОМОГАТЕЛЬНЫЕ --------

    /** Дедуп ролей по id + сохранение порядка первого появления. */
    private static List<Role> normalizeRoles(List<Role> roles) {
        if (roles == null || roles.isEmpty()) return List.of();
        Map<Object, Role> byId = new LinkedHashMap<>();
        for (Role r : roles) {
            if (r == null) continue;
            Object rid = r.getId();
            if (rid == null) throw new IllegalArgumentException("role id is required");
            byId.putIfAbsent(rid, r);
        }
        return new ArrayList<>(byId.values());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProjectMember member)) return false;
        return Objects.equals(getProjectId(), member.getProjectId()) && Objects.equals(getUser().getId(), member.getUser().getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getProjectId(), getUser());
    }
}
