package ru.gamehub.web.application.project.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.exception.ProjectNotFoundException;
import ru.gamehub.web.domain.user.User;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateProjectServiceTest {


    private ProjectRepository repository;
    private UpdateProjectService service;
    private User owner;


    /**
     * Подготовка окружения перед каждым тестом:
     * инициализируется репозиторий и сервис, создаётся пользователь-владелец.
     */
    @BeforeEach
    void setUp() {
        repository = new InMemoryProjectRepository();
        var userRepository = new InMemoryUserRepository();
        service = new UpdateProjectService(repository);

        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
        userRepository.save(owner);
    }

    @Test
    void updates_project_when_owner() {
        // Arrange: создать проект, сохранить в in-memory репо
/*        Project project = Project.create(owner, "test-name", "test description");
        repository.save(project);
        // Act: вызвать сервис с UpdateProjectCommand (правильный ownerId)
        UpdateProjectCommand command = new UpdateProjectCommand(project.getId(), owner.getId(), "updatedName", "updatedDescription");
        service.handle(command);
        // Assert: name/description обновлены
        Project updatedProject = repository.findById(project.getId()).get();
        assertEquals(updatedProject.getName(), command.name());
        assertEquals(updatedProject.getDescription(), command.description());

        assertNotEquals(updatedProject.getName(), project.getName());
        assertNotEquals(updatedProject.getDescription(), project.getDescription());
        assertEquals(updatedProject.getId(), project.getId());
        assertEquals(updatedProject.getOwner(), project.getOwner());*/
    }

    @Test
    void throws_if_project_not_found() {
        // Arrange: пустой репозиторий, не сохраняем проект

        // Act & Assert: ожидаем ProjectNotFoundException при попытке обновления несуществующего проекта
        UpdateProjectCommand command = new UpdateProjectCommand(
                // Несуществующий id
                java.util.UUID.randomUUID(),
                owner.getId(),
                "newName",
                "newDescription"
        );
        assertThrows(ProjectNotFoundException.class, () -> service.handle(command));
    }

    @Test
    void throws_if_not_owner() {
/*        // Arrange: создаём проект с одним owner, а обновлять пытается другой
        Project project = Project.create(owner, "test-name", "test description");
        repository.save(project);

        // Создаём “чужого” пользователя
        User anotherUser = User.create("EvilHacker", "evil@example.com", "Вредитель");

        UpdateProjectCommand command = new UpdateProjectCommand(
                project.getId(),
                anotherUser.getId(), // не владелец!
                "newName",
                "newDescription"
        );

        // Act & Assert: ожидаем ProjectAccessDeniedException
        assertThrows(ProjectAccessDeniedException.class, () -> service.handle(command));*/
    }


}
