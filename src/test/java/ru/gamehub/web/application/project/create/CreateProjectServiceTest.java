/*
package ru.gamehub.web.application.project.create;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.application.testinfra.repository.InMemoryUserRepository;
import ru.gamehub.web.application.user.get.GetUserService;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

*/
/**
 * Модульные тесты для {@link CreateProjectService}.
 * Проверяют корректность создания проекта, связывания с владельцем и установки временных меток.
 *//*

public class CreateProjectServiceTest {

    private ProjectRepository repository;
    private CreateProjectService service;
    private User owner;

    */
/**
     * Подготовка окружения перед каждым тестом:
     * инициализируется репозиторий и сервис, создаётся пользователь-владелец.
     *//*

    @BeforeEach
    void setUp() {
        repository = new InMemoryProjectRepository();
        var userRepository = new InMemoryUserRepository();
        var userService = new GetUserService(userRepository);
        service = new CreateProjectService(repository, userService);

        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
        userRepository.save(owner);
    }

    */
/**
     * Проверяет, что проект создаётся и сохраняется в репозиторий.
     * Также проверяется установка имени и генерация ID.
     *//*

    @Test
    void shouldCreateProject() {
        String name = "Test Project";
        String description = "A project for testing";

        CreateProjectCommand createProjectCommand = new CreateProjectCommand(owner.getId(), name, description);
        Project created = service.handle(createProjectCommand);

        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertTrue(repository.findById(created.getId()).isPresent());
    }

    */
/**
     * Проверяет, что владелец проекта корректно сохраняется и возвращается в доменном объекте.
     *//*

    @Test
    void shouldCreateProjectAndLinkToUser() {
        String name = "Test Project";
        String description = "A project for testing";

        CreateProjectCommand createProjectCommand = new CreateProjectCommand(owner.getId(), name, description);
        Project created = service.handle(createProjectCommand);

        assertNotNull(created.getId());
        assertEquals(name, created.getName());
        assertEquals(owner.getName(), created.getOwner().getName());
        assertEquals(owner.getHeadline(), created.getOwner().getHeadline());
        assertEquals(owner.getEmail(), created.getOwner().getEmail());
    }

    */
/**
     * Проверяет, что при создании проекта автоматически устанавливаются временные метки:
     * {@code createdAt} и {@code updatedAt}, и что они равны на момент создания.
     *//*

    @Test
    void shouldSetTimestampsOnProjectCreation() {
        String name = "Timestamp Test Project";
        String description = "Testing timestamps";

        CreateProjectCommand command = new CreateProjectCommand(owner.getId(), name, description);
        Project created = service.handle(command);

        assertNotNull(created.getCreatedAt(), "createdAt should not be null");
        assertNotNull(created.getUpdatedAt(), "updatedAt should not be null");
        assertEquals(created.getCreatedAt(), created.getUpdatedAt(), "createdAt and updatedAt should be equal on creation");
    }
}

*/
