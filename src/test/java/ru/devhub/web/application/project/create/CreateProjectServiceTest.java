package ru.devhub.web.application.project.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.devhub.web.application.project.ProjectAssembler;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectMemberRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryRoleRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.devhub.web.application.testinfra.repository.InMemoryUserRepository;
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

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateProjectCommandHandlerTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TechnologyRepository technologyRepository;
    private RoleRepository roleRepository;
    private ProjectTypeRepository typeRepository;
    private ProjectMemberRepository projectMemberRepository;

    private CreateProjectCommandHandler service;

    private User owner;
    private ProjectType webType;
    private Role devRole;
    private Role qaRole;
    private Technology javaTech;

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
        service = new CreateProjectCommandHandler(projectRepository, assembler, projectMemberRepository);

        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
        userRepository.save(owner);

        webType = ProjectType.create(UUID.randomUUID(), "Web");
        typeRepository.save(webType);

        // справочники для реалистичных тестов
        devRole = Role.create(1, "DEV");
        qaRole  = Role.create(2, "QA");
        roleRepository.save(devRole);
        roleRepository.save(qaRole);

        javaTech = Technology.create(1, "Java");
        technologyRepository.save(javaTech);
    }

    @Test
    void shouldCreateProject() {
        String name = "Test Project";
        String description = "A project for testing";
        String shortDescription = "Short desc";

        CreateProjectCommand command = new CreateProjectCommand(
                owner.getId(),
                name,
                description,
                shortDescription,
                webType.getId(),
                "DRAFT",
                List.of(), // technologyIds
                List.of(), // roleIds
                List.of()  // members
        );

        Project created = service.handle(command);

        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertEquals(description, created.getDescription());
        assertEquals(shortDescription, created.getShortDescription());
        assertEquals(ProjectStatus.DRAFT, created.getStatus());
        assertTrue(projectRepository.findById(created.getId()).isPresent());
    }

    @Test
    void shouldCreateProjectAndLinkToUser() {
        CreateProjectCommand command = new CreateProjectCommand(
                owner.getId(),
                "Test Project",
                "A project for testing",
                "Short desc",
                webType.getId(),
                "DRAFT",
                List.of(),
                List.of(),
                List.of()
        );

        Project created = service.handle(command);

        assertNotNull(created.getId());
        assertEquals(owner.getId(), created.getOwner().getId());
        assertEquals(owner.getName(), created.getOwner().getName());
        assertEquals(owner.getHeadline(), created.getOwner().getHeadline());
        assertEquals(owner.getEmail(), created.getOwner().getEmail());
    }

    @Test
    void shouldSetTimestampsOnProjectCreation() {
        CreateProjectCommand command = new CreateProjectCommand(
                owner.getId(),
                "Timestamp Test Project",
                "Testing timestamps",
                "Short desc",
                webType.getId(),
                "DRAFT",
                List.of(),
                List.of(),
                List.of()
        );

        Project created = service.handle(command);

        assertNotNull(created.getCreatedAt(), "createdAt should not be null");
        assertNotNull(created.getUpdatedAt(), "updatedAt should not be null");
        assertEquals(created.getCreatedAt(), created.getUpdatedAt(),
                "createdAt and updatedAt should be equal on creation");
    }

    // ===== добавленные тесты на участников =====

    @Test
    void creates_project_with_members_and_dedup_roles() {
        // owner как участник с дублями ролей
        CreateProjectCommand.Member mOwner = new CreateProjectCommand.Member(
                owner.getId(),
                ProjectMemberStatus.OWNER,
                List.of(devRole.getId(), devRole.getId(), devRole.getId())
        );

        // второй участник
        User bob = User.create("Bob", "bob@ex.com", "dev");
        userRepository.save(bob);

        // порядок: QA, QA, DEV => после дедупа ожидаем [QA, DEV]
        CreateProjectCommand.Member mBob = new CreateProjectCommand.Member(
                bob.getId(),
                ProjectMemberStatus.ACTIVE,
                List.of(qaRole.getId(), qaRole.getId(), devRole.getId())
        );

        CreateProjectCommand cmd = new CreateProjectCommand(
                owner.getId(),
                "Project with members",
                "desc",
                "short",
                webType.getId(),
                "DRAFT",
                List.of(javaTech.getId()),
                List.of(devRole.getId(), qaRole.getId()),
                List.of(mOwner, mBob)
        );

        Project created = service.handle(cmd);

        assertNotNull(created.getId());
        assertEquals(ProjectStatus.DRAFT, created.getStatus());
        assertEquals(2, created.getMembers().size());

        // владелец: статус OWNER, роли дедупнуты
        ProjectMember ownerMember = created.getMembers().stream()
                .filter(pm -> pm.getUser().getId().equals(owner.getId()))
                .findFirst().orElseThrow();
        assertEquals(ProjectMemberStatus.OWNER, ownerMember.getStatus());
        assertEquals(1, ownerMember.getRoles().size());
        assertEquals(devRole.getId(), ownerMember.getRoles().get(0).getId());

        // bob: статус ACTIVE, роли [QA, DEV] (порядок первого появления)
        ProjectMember bobMember = created.getMembers().stream()
                .filter(pm -> pm.getUser().getId().equals(bob.getId()))
                .findFirst().orElseThrow();
        assertEquals(ProjectMemberStatus.ACTIVE, bobMember.getStatus());
        assertEquals(List.of(qaRole.getId(), devRole.getId()),
                bobMember.getRoles().stream().map(Role::getId).toList());
    }

    @Test
    void members_have_projectId_of_created_project() {
        User alice = User.create("Alice", "alice@ex.com", "QA");
        userRepository.save(alice);

        CreateProjectCommand.Member m = new CreateProjectCommand.Member(
                alice.getId(),
                ProjectMemberStatus.ACTIVE,
                List.of(devRole.getId())
        );

        CreateProjectCommand cmd = new CreateProjectCommand(
                owner.getId(), "P", "D", "S", webType.getId(), "DRAFT",
                List.of(javaTech.getId()),
                List.of(devRole.getId()),
                List.of(m)
        );

        Project created = service.handle(cmd);

        UUID pid = created.getId();
        assertTrue(created.getMembers().stream().allMatch(pm -> pm.getProjectId().equals(pid)));
    }

    @Test
    void empty_members_list_results_in_no_members() {
        CreateProjectCommand cmd = new CreateProjectCommand(
                owner.getId(), "Empty", "D", "S", webType.getId(), "DRAFT",
                List.of(), List.of(), List.of() // пустой список
        );

        Project created = service.handle(cmd);

        assertNotNull(created.getId());
        assertTrue(created.getMembers() == null || created.getMembers().isEmpty(),
                "при пустом списке участников проект создаётся без members");
    }

    @Test
    void members_null_is_safe_and_does_not_fail() {
        // Тест проходит если в сервисе есть гард: if (members == null || members.isEmpty()) return project;
        CreateProjectCommand cmd = new CreateProjectCommand(
                owner.getId(), "NullMembers", "D", "S", webType.getId(), "DRAFT",
                List.of(), List.of(), null // members == null
        );

        Project created = service.handle(cmd);

        assertNotNull(created.getId());
        assertTrue(created.getMembers() == null || created.getMembers().isEmpty(),
                "при null members проект создаётся без участников");
    }
}
