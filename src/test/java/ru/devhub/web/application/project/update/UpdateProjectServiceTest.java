package ru.devhub.web.application.project.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.devhub.web.application.project.ProjectAssembler;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectMemberRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryRoleRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.devhub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.project.member.ProjectMember;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.domain.project.repository.ProjectMemberRepository;
import ru.devhub.web.domain.project.repository.ProjectRepository;
import ru.devhub.web.domain.reference.project.role.Role;
import ru.devhub.web.domain.reference.project.role.RoleRepository;
import ru.devhub.web.domain.reference.project.technology.Technology;
import ru.devhub.web.domain.reference.project.technology.TechnologyRepository;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.domain.reference.project.type.ProjectTypeRepository;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateProjectCommandHandlerTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TechnologyRepository technologyRepository;
    private RoleRepository roleRepository;
    private ProjectTypeRepository typeRepository;
    private UpdateProjectCommandHandler service;
    private ProjectMemberRepository projectMemberRepository;

    private User owner;
    private Role devRole;
    private Technology javaTech;
    private ProjectType webType;

    @BeforeEach
    void setUp() {
        projectRepository = new InMemoryProjectRepository();
        userRepository = new InMemoryUserRepository();
        technologyRepository = new InMemoryTechnologyRepository();
        roleRepository = new InMemoryRoleRepository();
        typeRepository = new InMemoryProjectTypeRepository();
        projectMemberRepository = new InMemoryProjectMemberRepository();

        ProjectAssembler assembler = new ProjectAssembler(
                userRepository, typeRepository, technologyRepository, roleRepository, projectMemberRepository
        );
        service = new UpdateProjectCommandHandler(projectRepository, assembler, projectMemberRepository);

        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
        userRepository.save(owner);

        devRole = Role.create(1, "DEV");
        javaTech = Technology.create(1, "Java");
        webType = ProjectType.create(UUID.randomUUID(), "Web");

        roleRepository.save(devRole);
        technologyRepository.save(javaTech);
        typeRepository.save(webType);
    }

    @Test
    void updates_project_with_all_fields() {
        UUID projectId = UUID.randomUUID();
        ProjectMember firstMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);
        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial desc")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole))
                .members(List.of(firstMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        User newMember = User.create("Newbie", "new@example.com", "Новичок");
        userRepository.save(newMember);
        Role qaRole = Role.create(2, "QA");
        roleRepository.save(qaRole);

        UpdateProjectCommand.Member updatedMember = new UpdateProjectCommand.Member(
                newMember.getId(), projectId, ProjectMemberStatus.ACTIVE, List.of(qaRole.getId()), OffsetDateTime.now(), null
        );
        UpdateProjectCommand.Member firstMemberInUpdate = new UpdateProjectCommand.Member(
                owner.getId(), projectId, ProjectMemberStatus.OWNER,
                firstMember.getRoles().stream().map(Role::getId).toList(),
                firstMember.getJoinedAt(), null
        );

        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getId(),
                owner.getId(),
                "UpdatedName",
                "UpdatedDescription",
                "ShortUpdated",
                ProjectStatus.ACTIVE.name(),
                webType.getId(),
                List.of(javaTech.getId()),
                List.of(devRole.getId(), qaRole.getId()),
                List.of(firstMemberInUpdate, updatedMember)
        );

        service.handle(command);

        Project updated = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals("UpdatedName", updated.getName());
        assertEquals("UpdatedDescription", updated.getDescription());
        assertEquals("ShortUpdated", updated.getShortDescription());
        assertEquals(ProjectStatus.ACTIVE, updated.getStatus());
        assertEquals(List.of(javaTech), updated.getTechnologies());
        assertEquals(2, updated.getRoles().size());
        assertTrue(updated.getRoles().stream().anyMatch(r -> r.getName().equals("QA")));
        assertEquals(2, updated.getMembers().size());
        assertTrue(updated.getMembers().stream().anyMatch(m -> m.getUser().getId().equals(newMember.getId())));
        assertTrue(updated.getMembers().stream().flatMap(m -> m.getRoles().stream()).anyMatch(r -> r.getId().equals(qaRole.getId())));
    }

    @Test
    void throws_if_project_not_found() {
        UpdateProjectCommand command = new UpdateProjectCommand(
                UUID.randomUUID(), owner.getId(),
                "newName", "newDescription", "short", "ACTIVE", webType.getId(),
                List.of(), List.of(), List.of()
        );
        assertThrows(ProjectNotFoundException.class, () -> service.handle(command));
    }

    @Test
    void throws_if_not_owner() {
        Project project = Project.builder()
                .owner(owner).name("test-name")
                .description("test-description")
                .shortDescription("short-description")
                .type(webType)
                .build();

        projectRepository.save(project);
        User anotherUser = User.create("EvilHacker", "evil@example.com", "Вредитель");
        userRepository.save(anotherUser);

        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getId(), anotherUser.getId(),
                "newName", "newDescription", "short", "ACTIVE", webType.getId(),
                List.of(), List.of(), List.of()
        );

        assertThrows(ProjectAccessDeniedException.class, () -> service.handle(command));
    }

    @Test
    void when_members_null_members_unchanged() {
        // given: проект с owner + еще один участник
        UUID projectId = UUID.randomUUID();
        User u = User.create("Alice", "alice@ex.com", "dev");
        userRepository.save(u);
        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);
        ProjectMember alice = ProjectMember.create(projectId, u, List.of(devRole), ProjectMemberStatus.ACTIVE);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole))
                .members(List.of(ownerMember, alice))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        UpdateProjectCommand command = new UpdateProjectCommand(
                projectId, owner.getId(),
                "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(),
                List.of(javaTech.getId()),
                List.of(devRole.getId()),
                null // <--- members == null
        );

        Project updated = service.handle(command);

        assertEquals(2, updated.getMembers().size(), "состав не должен меняться при members=null");
        assertTrue(updated.getMembers().stream().anyMatch(m -> m.getUser().getId().equals(u.getId())));
        assertTrue(updated.getMembers().stream().anyMatch(m -> m.getUser().getId().equals(owner.getId())));
    }

    @Test
    void when_members_empty_deletes_everyone_except_owner() {
        UUID projectId = UUID.randomUUID();
        User bob = User.create("Bob", "bob@ex.com", "dev");
        userRepository.save(bob);

        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);
        ProjectMember bobMember = ProjectMember.create(projectId, bob, List.of(devRole), ProjectMemberStatus.ACTIVE);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole))
                .members(List.of(ownerMember, bobMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        UpdateProjectCommand command = new UpdateProjectCommand(
                projectId, owner.getId(),
                "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(),
                List.of(javaTech.getId()),
                List.of(devRole.getId()),
                List.of() // пустой список -> удалить всех, кроме OWNER
        );

        Project updated = service.handle(command);

        assertEquals(1, updated.getMembers().size());
        assertEquals(owner.getId(), updated.getMembers().get(0).getUser().getId());
        assertEquals(ProjectMemberStatus.OWNER, updated.getMembers().get(0).getStatus());
    }

    @Test
    void updates_non_owner_roles_and_status_with_dedup() {
        UUID projectId = UUID.randomUUID();
        User bob = User.create("Bob", "bob@ex.com", "dev");
        userRepository.save(bob);

        Role qa = Role.create(2, "QA");
        roleRepository.save(qa);

        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);
        ProjectMember bobMember = ProjectMember.create(projectId, bob, List.of(devRole), ProjectMemberStatus.INVITED);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole, qa))
                .members(List.of(ownerMember, bobMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        // Во входе — Bob с ролями [QA, QA, DEV] и статусом ACTIVE
        UpdateProjectCommand.Member bobIn = new UpdateProjectCommand.Member(
                bob.getId(), projectId, ProjectMemberStatus.ACTIVE,
                List.of(qa.getId(), qa.getId(), devRole.getId()), null, null
        );
        UpdateProjectCommand.Member ownerIn = new UpdateProjectCommand.Member(
                owner.getId(), projectId, ProjectMemberStatus.OWNER,
                List.of(devRole.getId()), null, null
        );

        UpdateProjectCommand cmd = new UpdateProjectCommand(
                projectId, owner.getId(), "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(), List.of(javaTech.getId()), List.of(devRole.getId(), qa.getId()),
                List.of(ownerIn, bobIn)
        );

        Project updated = service.handle(cmd);

        ProjectMember bobUpdated = updated.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(bob.getId()))
                .findFirst().orElseThrow();

        assertEquals(ProjectMemberStatus.ACTIVE, bobUpdated.getStatus());
        // дедуп + порядок первого появления: QA, DEV
        assertEquals(List.of(qa.getId(), devRole.getId()),
                bobUpdated.getRoles().stream().map(Role::getId).toList());
    }

    @Test
    void ignores_owner_status_downgrade_but_allows_role_update() {
        UUID projectId = UUID.randomUUID();

        Role qa = Role.create(2, "QA");
        roleRepository.save(qa);

        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole, qa))
                .members(List.of(ownerMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        // Пытаемся "понизить" OWNER до ACTIVE и сменить роли на [QA]
        UpdateProjectCommand.Member ownerIn = new UpdateProjectCommand.Member(
                owner.getId(), projectId, ProjectMemberStatus.ACTIVE, // будет проигнорирован по статусу
                List.of(qa.getId()), null, null
        );

        UpdateProjectCommand cmd = new UpdateProjectCommand(
                projectId, owner.getId(), "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(), List.of(javaTech.getId()), List.of(devRole.getId(), qa.getId()),
                List.of(ownerIn)
        );

        Project updated = service.handle(cmd);

        ProjectMember ownerAfter = updated.getMembers().get(0);
        assertEquals(ProjectMemberStatus.OWNER, ownerAfter.getStatus(), "понижение OWNER запрещено");
        assertEquals(List.of(qa.getId()),
                ownerAfter.getRoles().stream().map(Role::getId).toList(),
                "роли владельца можно обновлять"
        );
    }

    @Test
    void no_update_when_roles_set_equal_and_status_equal() {
        UUID projectId = UUID.randomUUID();
        User bob = User.create("Bob", "bob@ex.com", "dev");
        userRepository.save(bob);

        Role qa = Role.create(2, "QA");
        roleRepository.save(qa);

        // У Боба роли в порядке [DEV, QA]
        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);
        ProjectMember bobMember = ProjectMember.create(projectId, bob, List.of(devRole, qa), ProjectMemberStatus.ACTIVE);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole, qa))
                .members(List.of(ownerMember, bobMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        // Во входе — тот же набор ролей, но в ином порядке + дубликат; статус тот же
        UpdateProjectCommand.Member bobIn = new UpdateProjectCommand.Member(
                bob.getId(), projectId, ProjectMemberStatus.ACTIVE,
                List.of(qa.getId(), devRole.getId(), qa.getId()), null, null
        );
        UpdateProjectCommand.Member ownerIn = new UpdateProjectCommand.Member(
                owner.getId(), projectId, ProjectMemberStatus.OWNER, List.of(devRole.getId()), null, null
        );

        UpdateProjectCommand cmd = new UpdateProjectCommand(
                projectId, owner.getId(), "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(), List.of(javaTech.getId()), List.of(devRole.getId(), qa.getId()),
                List.of(ownerIn, bobIn)
        );

        Project updated = service.handle(cmd);

        // Порядок у Боба должен остаться прежним (не было апдейта)
        ProjectMember bobAfter = updated.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(bob.getId()))
                .findFirst().orElseThrow();

        assertEquals(List.of(devRole.getId(), qa.getId()),
                bobAfter.getRoles().stream().map(Role::getId).toList(),
                "так как множества равны и статус не менялся — апдейта быть не должно, порядок остался прежним"
        );
    }

    @Test
    void add_member_roles_dedup_preserve_order() {
        UUID projectId = UUID.randomUUID();
        Role qa = Role.create(2, "QA");
        roleRepository.save(qa);

        ProjectMember ownerMember = ProjectMember.create(projectId, owner, List.of(devRole), ProjectMemberStatus.OWNER);

        Project project = Project.builder()
                .id(projectId)
                .owner(owner)
                .name("Initial")
                .description("Initial")
                .shortDescription("short")
                .type(webType)
                .status(ProjectStatus.DRAFT)
                .technologies(List.of(javaTech))
                .roles(List.of(devRole, qa))
                .members(List.of(ownerMember))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        User eve = User.create("Eve", "eve@ex.com", "qa");
        userRepository.save(eve);

        UpdateProjectCommand.Member eveIn = new UpdateProjectCommand.Member(
                eve.getId(), projectId, ProjectMemberStatus.ACTIVE,
                List.of(devRole.getId(), devRole.getId(), qa.getId()), null, null
        );
        UpdateProjectCommand.Member ownerIn = new UpdateProjectCommand.Member(
                owner.getId(), projectId, ProjectMemberStatus.OWNER, List.of(devRole.getId()), null, null
        );

        UpdateProjectCommand cmd = new UpdateProjectCommand(
                projectId, owner.getId(), "N", "D", "S", ProjectStatus.ACTIVE.name(),
                webType.getId(), List.of(javaTech.getId()), List.of(devRole.getId(), qa.getId()),
                List.of(ownerIn, eveIn)
        );

        Project updated = service.handle(cmd);

        ProjectMember eveAfter = updated.getMembers().stream()
                .filter(m -> m.getUser().getId().equals(eve.getId()))
                .findFirst().orElseThrow();

        assertEquals(List.of(devRole.getId(), qa.getId()),
                eveAfter.getRoles().stream().map(Role::getId).toList(),
                "дедуп и порядок первого появления (DEV затем QA)"
        );
    }
}
