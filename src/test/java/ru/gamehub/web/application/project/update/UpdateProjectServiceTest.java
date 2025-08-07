package ru.gamehub.web.application.project.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectMemoryRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectTypeRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryRoleRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryTechnologyRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.ProjectStatus;
import ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.gamehub.web.domain.project.exception.ProjectNotFoundException;
import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.domain.reference.project.role.Role;
import ru.gamehub.web.domain.reference.project.role.RoleRepository;
import ru.gamehub.web.domain.reference.project.technology.Technology;
import ru.gamehub.web.domain.reference.project.technology.TechnologyRepository;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.reference.project.type.ProjectTypeRepository;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateProjectServiceTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TechnologyRepository technologyRepository;
    private RoleRepository roleRepository;
    private ProjectTypeRepository typeRepository;
    private UpdateProjectService service;
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
        projectMemberRepository = new InMemoryProjectMemoryRepository();

        // Создаём assembler с зависимостями
        ProjectAggregateAssembler assembler = new ProjectAggregateAssembler(
                userRepository, typeRepository, technologyRepository, roleRepository, projectMemberRepository
        );

        // Сервис теперь зависит от assembler + projectRepository
        service = new UpdateProjectService(projectRepository, assembler);

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
        // Arrange: создать проект, сохранить в in-memory репо
        UUID projectId = UUID.randomUUID();
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
                .members(List.of(
                        ProjectMember.create(projectId, owner, devRole, OffsetDateTime.now())
                ))
                .createdAt(OffsetDateTime.now())
                .build();
        projectRepository.save(project);

        // Новый пользователь и роль для update
        User newMember = User.create("Newbie", "new@example.com", "Новичок");
        userRepository.save(newMember);
        Role qaRole = Role.create(2, "QA");
        roleRepository.save(qaRole);

        UpdateProjectCommand.Member updatedMember = new UpdateProjectCommand.Member(
                newMember.getId(), qaRole.getId(), OffsetDateTime.now()
        );

        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getId(),
                owner.getId(),
                "UpdatedName",
                "UpdatedDescription",
                "ShortUpdated",
                ProjectStatus.ACTIVE.name(),
                UUID.randomUUID(),
                List.of(javaTech.getId()),
                List.of(devRole.getId(), qaRole.getId()),
                List.of(updatedMember)
        );

        // Act
        service.handle(command);

        // Assert: все поля обновлены
        Project updated = projectRepository.findById(project.getId()).orElseThrow();

        assertEquals("UpdatedName", updated.getName());
        assertEquals("UpdatedDescription", updated.getDescription());
        assertEquals("ShortUpdated", updated.getShortDescription());
        assertEquals(ProjectStatus.ACTIVE, updated.getStatus());
        assertEquals(List.of(javaTech), updated.getTechnologies());
        assertEquals(2, updated.getRoles().size());
        assertTrue(updated.getRoles().stream().anyMatch(r -> r.getName().equals("QA")));
        assertEquals(1, updated.getMembers().size());
        assertEquals(newMember.getId(), updated.getMembers().get(0).getUser().getId());
        assertEquals(qaRole.getId(), updated.getMembers().get(0).getRole().getId());
    }

    @Test
    void throws_if_project_not_found() {
        UpdateProjectCommand command = new UpdateProjectCommand(
                UUID.randomUUID(), owner.getId(),
                "newName", "newDescription", "short", "ACTIVE", UUID.randomUUID(),
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
                .build();

        projectRepository.save(project);
        User anotherUser = User.create("EvilHacker", "evil@example.com", "Вредитель");
        userRepository.save(anotherUser);

        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getId(), anotherUser.getId(),
                "newName", "newDescription", "short", "ACTIVE", UUID.randomUUID(),
                List.of(), List.of(), List.of()
        );

        assertThrows(ProjectAccessDeniedException.class, () -> service.handle(command));
    }
}
