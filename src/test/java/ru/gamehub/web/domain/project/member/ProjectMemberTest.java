package ru.gamehub.web.domain.project.member;

import org.junit.jupiter.api.Test;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.user.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProjectMemberTest {

    private static Role role(int id, String name) {
        return Role.create(id, name);
    }

    private static User user(String name) {
        return User.create(name, name.toLowerCase() + "@ex.com", "headline");
    }

    @Test
    void factory_create_minimal_dedup_roles_preserve_order() {
        UUID projectId = UUID.randomUUID();
        User u = user("Nikita");

        // Повторяющиеся роли → должны дедупиться, порядок первого появления сохраняется
        List<Role> roles = List.of(role(1, "DEV"), role(2, "QA"), role(1, "DEV"));

        ProjectMember pm = ProjectMember.create(projectId, u, roles, ProjectMemberStatus.ACTIVE);

        assertNotNull(pm.getId(), "id должен генериться");
        assertEquals(projectId, pm.getProjectId());
        assertEquals(u, pm.getUser());
        assertEquals(ProjectMemberStatus.ACTIVE, pm.getStatus());
        assertNotNull(pm.getJoinedAt(), "joinedAt должен выставляться");
        assertNull(pm.getLeftAt(), "leftAt по умолчанию null");

        // дедуп + порядок
        assertEquals(2, pm.getRoles().size());
        assertEquals(1, pm.getRoles().get(0).getId());
        assertEquals(2, pm.getRoles().get(1).getId());
    }

    @Test
    void factory_create_full_keeps_explicit_values() {
        UUID id = UUID.randomUUID();
        UUID projectId = UUID.randomUUID();
        User u = user("Daria");
        List<Role> roles = List.of(role(2, "QA"));
        OffsetDateTime joined = OffsetDateTime.now().minusDays(1);
        OffsetDateTime left = OffsetDateTime.now();

        ProjectMember pm = ProjectMember.create(id, projectId, u, roles, ProjectMemberStatus.INVITED, joined, left);

        assertEquals(id, pm.getId());
        assertEquals(projectId, pm.getProjectId());
        assertEquals(u, pm.getUser());
        assertEquals(ProjectMemberStatus.INVITED, pm.getStatus());
        assertEquals(joined, pm.getJoinedAt());
        assertEquals(left, pm.getLeftAt());
    }

    @Test
    void withRoles_returns_new_instance_and_replaces_roles_deduped() {
        UUID projectId = UUID.randomUUID();
        User u = user("Alex");
        ProjectMember pm = ProjectMember.create(projectId, u, List.of(role(1, "DEV")), ProjectMemberStatus.ACTIVE);

        List<Role> newRoles = List.of(role(3, "OPS"), role(3, "OPS"), role(2, "QA"));
        ProjectMember updated = pm.withRoles(newRoles);

        assertNotSame(pm, updated, "должен вернуться новый инстанс");
        assertEquals(pm.getId(), updated.getId());
        assertEquals(pm.getProjectId(), updated.getProjectId());
        assertEquals(pm.getUser(), updated.getUser());
        assertEquals(pm.getStatus(), updated.getStatus());
        assertEquals(pm.getJoinedAt(), updated.getJoinedAt());
        assertEquals(pm.getLeftAt(), updated.getLeftAt());

        // роли заменены и дедупнуты с сохранением порядка первого появления
        assertEquals(List.of(3, 2), updated.getRoles().stream().map(Role::getId).toList());
        // исходный объект не изменился
        assertEquals(List.of(1), pm.getRoles().stream().map(Role::getId).toList());
    }

    @Test
    void withStatus_allows_change_for_non_owner() {
        UUID projectId = UUID.randomUUID();
        User u = user("Mike");
        ProjectMember pm = ProjectMember.create(projectId, u, List.of(), ProjectMemberStatus.ACTIVE);

        ProjectMember updated = pm.withStatus(ProjectMemberStatus.INVITED);

        assertNotSame(pm, updated);
        assertEquals(ProjectMemberStatus.INVITED, updated.getStatus());
        // инварианты
        assertEquals(pm.getId(), updated.getId());
        assertEquals(pm.getProjectId(), updated.getProjectId());
        assertEquals(pm.getUser(), updated.getUser());
        assertEquals(pm.getRoles(), updated.getRoles());
        assertEquals(pm.getJoinedAt(), updated.getJoinedAt());
        assertEquals(pm.getLeftAt(), updated.getLeftAt());
    }

    @Test
    void withStatus_for_owner_throws_on_downgrade() {
        UUID projectId = UUID.randomUUID();
        User u = user("Owner");
        ProjectMember owner = ProjectMember.create(projectId, u, List.of(role(1, "DEV")), ProjectMemberStatus.OWNER);

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> owner.withStatus(ProjectMemberStatus.ACTIVE));
        assertTrue(ex.getMessage().toLowerCase().contains("owner"), "сообщение должно быть про запрет изменения OWNER");
    }

    @Test
    void withLeftAt_sets_timestamp_when_null() {
        UUID projectId = UUID.randomUUID();
        User u = user("Alex");
        ProjectMember pm = ProjectMember.create(projectId, u, List.of(), ProjectMemberStatus.ACTIVE);

        ProjectMember left = pm.withLeftAt(null);

        assertNotSame(pm, left);
        assertNotNull(left.getLeftAt());
        // остальное без изменений
        assertEquals(pm.getId(), left.getId());
        assertEquals(pm.getProjectId(), left.getProjectId());
        assertEquals(pm.getUser(), left.getUser());
        assertEquals(pm.getRoles(), left.getRoles());
        assertEquals(pm.getStatus(), left.getStatus());
        assertEquals(pm.getJoinedAt(), left.getJoinedAt());
    }

    @Test
    void equals_and_hashCode_based_on_projectId_and_userId() {
        UUID projectId = UUID.randomUUID();
        User u1 = user("U1");
        User u2 = user("U2");

        ProjectMember a = ProjectMember.create(projectId, u1, List.of(role(1, "DEV")), ProjectMemberStatus.ACTIVE);
        ProjectMember b = ProjectMember.create(projectId, u1, List.of(role(2, "QA")), ProjectMemberStatus.OWNER); // другой id, но тот же user

        assertEquals(a, b, "одинаковый (projectId, userId) → equals true");
        assertEquals(a.hashCode(), b.hashCode());

        ProjectMember c = ProjectMember.create(projectId, u2, List.of(role(1, "DEV")), ProjectMemberStatus.ACTIVE);
        assertNotEquals(a, c);
    }

    @Test
    void constructor_guards_on_nulls() {
        UUID projectId = UUID.randomUUID();
        User u = user("Zoe");
        Role r = role(1, "DEV");

        assertThrows(NullPointerException.class,
                () -> ProjectMember.create(projectId, null, List.of(r), ProjectMemberStatus.ACTIVE));

        assertThrows(NullPointerException.class,
                () -> ProjectMember.create(null, u, List.of(r), ProjectMemberStatus.ACTIVE));

        assertThrows(NullPointerException.class,
                () -> ProjectMember.create(projectId, u, List.of(r), null));
    }
}
