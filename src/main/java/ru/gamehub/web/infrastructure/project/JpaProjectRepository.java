package ru.gamehub.web.infrastructure.project;

import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JpaProjectRepository implements ProjectRepository {
    @Override
    public void save(Project project) {

    }

    @Override
    public Optional<Project> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Project> findAll() {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }
}
