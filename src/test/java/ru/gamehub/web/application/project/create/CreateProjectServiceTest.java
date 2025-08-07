package ru.gamehub.web.application.project.create;

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
import ru.gamehub.web.domain.project.member.ProjectMemberRepository;
import ru.gamehub.web.domain.reference.project.role.RoleRepository;
import ru.gamehub.web.domain.reference.project.technology.TechnologyRepository;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.reference.project.type.ProjectTypeRepository;
import ru.gamehub.web.domain.user.User;
import ru.gamehub.web.domain.user.UserRepository;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateProjectServiceTest {

    private ProjectRepository projectRepository;
    private UserRepository userRepository;
    private TechnologyRepository technologyRepository;
    private RoleRepository roleRepository;
    private ProjectTypeRepository typeRepository;
    private CreateProjectService service;
    private ProjectMemberRepository projectMemberRepository;
    private User owner;
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
        service = new CreateProjectService(projectRepository, assembler);
        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
        userRepository.save(owner);

        // Синтетический тип проекта для теста
        webType = ProjectType.create(UUID.randomUUID(), "Web");
        typeRepository.save(webType);
    }


    @Test
    void shouldCreateProject() {
        String name = "Test Project";
        String description = "A project for testing";
        String shortDescription = "Short desc";

        // Пример с расширенной CreateProjectCommand (дополни под свою сигнатуру!)
        CreateProjectCommand command = new CreateProjectCommand(
                owner.getId(),
                name,
                description,
                shortDescription,
                webType.getId(),
                "DRAFT",
                List.of(), // technologyIds
                List.of(), // roleIds
                List.of()  // members (или memberIds)
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
        String name = "Timestamp Test Project";
        String description = "Testing timestamps";
        String shortDescription = "Short desc";

        CreateProjectCommand command = new CreateProjectCommand(
                owner.getId(),
                name,
                description,
                shortDescription,
                webType.getId(),
                "DRAFT",
                List.of(),
                List.of(),
                List.of()
        );
        Project created = service.handle(command);

        assertNotNull(created.getCreatedAt(), "createdAt should not be null");
        assertNotNull(created.getUpdatedAt(), "updatedAt should not be null");
        assertEquals(created.getCreatedAt(), created.getUpdatedAt(), "createdAt and updatedAt should be equal on creation");
    }
}

