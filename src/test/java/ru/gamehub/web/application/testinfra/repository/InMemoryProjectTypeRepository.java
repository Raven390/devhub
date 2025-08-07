package ru.gamehub.web.application.testinfra.repository;

import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.reference.project.type.ProjectTypePage;
import ru.gamehub.web.domain.reference.project.type.ProjectTypeRepository;

import java.util.UUID;

public class InMemoryProjectTypeRepository extends BaseInMemoryRepository<ProjectType, UUID> implements ProjectTypeRepository {
    @Override
    protected UUID getId(ProjectType entity) {
        return entity.getId();
    }

    // TODO
    @Override
    public ProjectTypePage findPage() {
        return null;
    }
}
