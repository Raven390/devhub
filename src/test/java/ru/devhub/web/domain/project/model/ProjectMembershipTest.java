package ru.devhub.web.domain.project.model;

import org.junit.jupiter.api.Test;
import ru.devhub.web.domain.project.exception.InvalidProjectStatusException;
import ru.devhub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.devhub.web.domain.project.exception.UserAlreadyInProjectException;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.user.User;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMembershipTest {

    private final User owner = User.create("Owner", "owner@example.com", "Founder");
    private final User candidate = User.create("Candidate", "candidate@example.com", "Junior");
    private final User anotherUser = User.create("Another", "another@example.com", "Senior");
    private final Role backend = Role.create(1, "Backend");

    @Test
    void addMember_success() {
        Project project = project(ProjectStatus.RECRUITING, ownerMember());

        Project updated = project.addMember(candidate, List.of(backend), ProjectMemberStatus.INVITED);

        assertEquals(2, updated.getMembers().size());
        ProjectMember added = updated.getMembers().stream()
                .filter(member -> member.getUser().getId().equals(candidate.getId()))
                .findFirst()
                .orElseThrow();
        assertEquals(ProjectMemberStatus.INVITED, added.getStatus());
        assertEquals(List.of(backend.getId()), added.getRoles().stream().map(Role::getId).toList());
    }

    @Test
    void addMember_throws_when_already_member() {
        ProjectMember activeMember = ProjectMember.create(
                UUID.randomUUID(), candidate, List.of(backend), ProjectMemberStatus.ACTIVE);
        Project project = project(ProjectStatus.RECRUITING, ownerMember(), activeMember);

        assertThrows(UserAlreadyInProjectException.class,
                () -> project.addMember(candidate, List.of(backend), ProjectMemberStatus.INVITED));
    }

    @Test
    void addMember_throws_when_project_draft() {
        Project project = project(ProjectStatus.DRAFT, ownerMember());

        assertThrows(InvalidProjectStatusException.class,
                () -> project.addMember(candidate, List.of(backend), ProjectMemberStatus.INVITED));
    }

    @Test
    void removeMember_owner_cannot_be_removed() {
        ProjectMember ownerMember = ownerMember();
        Project project = project(ProjectStatus.ACTIVE, ownerMember);

        assertThrows(ProjectAccessDeniedException.class,
                () -> project.removeMember(ownerMember.getId(), owner.getId()));
    }

    @Test
    void removeMember_self_leave_success() {
        ProjectMember activeMember = ProjectMember.create(
                UUID.randomUUID(), candidate, List.of(backend), ProjectMemberStatus.ACTIVE);
        Project project = project(ProjectStatus.ACTIVE, ownerMember(), activeMember);

        Project updated = project.removeMember(activeMember.getId(), candidate.getId());

        ProjectMember removed = updated.findMemberById(activeMember.getId());
        assertEquals(ProjectMemberStatus.LEFT, removed.getStatus());
        assertTrue(removed.getLeftAt() != null);
    }

    @Test
    void removeMember_owner_can_kick() {
        ProjectMember activeMember = ProjectMember.create(
                UUID.randomUUID(), candidate, List.of(backend), ProjectMemberStatus.ACTIVE);
        Project project = project(ProjectStatus.ACTIVE, ownerMember(), activeMember);

        Project updated = project.removeMember(activeMember.getId(), owner.getId());

        ProjectMember removed = updated.findMemberById(activeMember.getId());
        assertEquals(ProjectMemberStatus.REMOVED, removed.getStatus());
        assertTrue(removed.getLeftAt() != null);
    }

    @Test
    void removeMember_non_owner_cannot_kick() {
        ProjectMember activeMember = ProjectMember.create(
                UUID.randomUUID(), candidate, List.of(backend), ProjectMemberStatus.ACTIVE);
        Project project = project(ProjectStatus.ACTIVE, ownerMember(), activeMember);

        assertThrows(ProjectAccessDeniedException.class,
                () -> project.removeMember(activeMember.getId(), anotherUser.getId()));
    }

    private Project project(ProjectStatus status, ProjectMember... members) {
        return Project.builder()
                .owner(owner)
                .name("DevHub")
                .description("Team project")
                .shortDescription("Team")
                .type(ProjectType.create(UUID.randomUUID(), "Web"))
                .status(status)
                .technologies(List.of())
                .roles(List.of(backend))
                .members(List.of(members))
                .build();
    }

    private ProjectMember ownerMember() {
        return ProjectMember.create(UUID.randomUUID(), owner, List.of(backend), ProjectMemberStatus.OWNER);
    }
}
