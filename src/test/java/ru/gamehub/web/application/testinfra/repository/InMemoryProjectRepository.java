package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;

import java.util.UUID;

/**
 * In-memory реализация {@link ProjectRepository}, основанная на {@link BaseInMemoryRepository}.
 */
public class InMemoryProjectRepository extends BaseInMemoryRepository<Project> implements ProjectRepository {

    @Override
    protected UUID getId(Project project) {
        return project.getId();
    }
}
