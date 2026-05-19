package ru.devhub.api.application.project.assembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.devhub.api.application.project.command.ListProjectsCommand;
import ru.devhub.api.application.testinfra.repository.InMemoryProjectRepository;
import ru.devhub.api.domain.project.model.Project;
import ru.devhub.api.domain.project.model.ProjectPage;
import ru.devhub.api.domain.project.model.ProjectStatus;
import ru.devhub.api.domain.user.User;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ListProjectsServiceTest {

    private InMemoryProjectRepository repo;
    private ListProjectsService service;
    private User owner;

    @BeforeEach
    void setUp() {
        repo = new InMemoryProjectRepository();
        service = new ListProjectsService(repo);
        owner = User.create("Nikita", "nikita@example.com", "Геймдев-разработчик");
    }

    @Test
    void returns_all_projects_in_single_page() {
        Project p1 = Project.builder()
                .owner(owner)
                .name("First")
                .description("Desc1")
                .shortDescription("Short 1")
                .status(ProjectStatus.DRAFT)
                .technologies(List.of())
                .roles(List.of())
                .members(List.of())
                .createdAt(OffsetDateTime.now())
                .build();

        Project p2 = Project.builder()
                .owner(owner)
                .name("Second")
                .description("Desc2")
                .shortDescription("Short 2")
                .status(ProjectStatus.DRAFT)
                .technologies(List.of())
                .roles(List.of())
                .members(List.of())
                .createdAt(OffsetDateTime.now())
                .build();

        repo.save(p1);
        repo.save(p2);

        // Страница 0, размер 10 — обе записи на первой странице
        ListProjectsCommand command = new ListProjectsCommand(0, 10);
        ProjectPage page = service.handle(command);

        assertEquals(2, page.getProjects().size());
        assertEquals(2, page.getTotal());
        assertEquals(0, page.getPage());
        assertEquals(10, page.getSize());
        assertTrue(page.getProjects().stream().anyMatch(p -> p.getName().equals("First")));
    }

    @Test
    void returns_page_slice_for_large_dataset() {
        // 25 проектов
        for (int i = 0; i < 25; i++) {
            Project project = Project.builder()
                    .owner(owner)
                    .name("project #" + i)
                    .description("Desc" + i)
                    .shortDescription("Short" + i)
                    .status(ProjectStatus.DRAFT)
                    .technologies(List.of())
                    .roles(List.of())
                    .members(List.of())
                    .createdAt(OffsetDateTime.now())
                    .build();
            repo.save(project);
        }

        // Страница 1, размер 10 — это 2-я десятка (10-19)
        ListProjectsCommand command = new ListProjectsCommand(1, 10);
        ProjectPage page = service.handle(command);

        assertEquals(10, page.getProjects().size());
        assertEquals(25, page.getTotal());
        assertEquals(1, page.getPage());
        assertEquals(10, page.getSize());
    }

    @Test
    void returns_empty_page_when_out_of_range() {
        // 5 проектов
        for (int i = 0; i < 5; i++) {
            Project project = Project.builder()
                    .owner(owner)
                    .name("project #" + i)
                    .description("Desc" + i)
                    .shortDescription("Short" + i)
                    .status(ProjectStatus.DRAFT)
                    .technologies(List.of())
                    .roles(List.of())
                    .members(List.of())
                    .createdAt(OffsetDateTime.now())
                    .build();
            repo.save(project);
        }

        // Страница 2, размер 10 — не должно быть записей
        ListProjectsCommand command = new ListProjectsCommand(2, 10);
        ProjectPage page = service.handle(command);

        assertEquals(0, page.getProjects().size());
        assertEquals(5, page.getTotal());
        assertEquals(2, page.getPage());
        assertEquals(10, page.getSize());
    }

    @Test
    void returns_empty_page_when_no_projects() {
        ListProjectsCommand command = new ListProjectsCommand(0, 10);
        ProjectPage page = service.handle(command);

        assertEquals(0, page.getProjects().size());
        assertEquals(0, page.getTotal());
        assertEquals(0, page.getPage());
        assertEquals(10, page.getSize());
    }

    @Test
    void throws_on_invalid_page_or_size() {
        assertThrows(IllegalArgumentException.class, () -> new ListProjectsCommand(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> new ListProjectsCommand(0, 0));
    }
}
