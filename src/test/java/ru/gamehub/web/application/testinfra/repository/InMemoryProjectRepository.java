package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.project.ProjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * In-memory реализация {@link ProjectRepository}, основанная на {@link BaseInMemoryRepository}.
 */
public class InMemoryProjectRepository extends BaseInMemoryRepository<Project> implements ProjectRepository {

    @Override
    protected UUID getId(Project project) {
        return project.getId();
    }

    @Override
    public ProjectPage findPage(int page, int size) {
        // Безопасность: корректируем некорректные параметры
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");

        List<Project> all = new ArrayList<>(store.values()); // store — твой Map<UUID, Project>
        int total = all.size();

        // Сортировка по какому-то критерию, если нужно (например, по дате создания)
        // all.sort(...);

        int fromIndex = Math.min(page * size, total); // если страница за пределами — пусто
        int toIndex = Math.min(fromIndex + size, total);

        List<Project> pageList = (fromIndex < toIndex) ? all.subList(fromIndex, toIndex) : List.of();

        return ProjectPage.create(pageList, total, page, size);
    }

}
