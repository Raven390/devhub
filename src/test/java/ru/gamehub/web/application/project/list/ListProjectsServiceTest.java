/*
package ru.gamehub.web.application.project.list;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.gamehub.web.application.testinfra.repository.InMemoryProjectRepository;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.user.User;

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
        Project p1 = Project.create(owner, "First", "Desc1");
        Project p2 = Project.create(owner, "Second", "Desc2");
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
        for (int i = 0; i < 25; i++)
            repo.save(Project.create(owner, "Proj" + i, "Desc" + i));

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
        for (int i = 0; i < 5; i++)
            repo.save(Project.create(owner, "Proj" + i, "Desc" + i));

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
*/
