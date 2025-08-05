package ru.gamehub.web.infrastructure.jpa.project.type;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.domain.project.type.ProjectTypeRepository;
import ru.gamehub.web.domain.project.type.exception.ProjectTypeNotFoundException;
import ru.gamehub.web.infrastructure.jpa.project.type.mapper.ProjectTypeJpaMapper;

import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectTypeRepositoryAdapter implements ProjectTypeRepository {
    private final ProjectTypeJpaRepository jpaRepository;
    private final ProjectTypeJpaMapper mapper;

    public ProjectTypeRepositoryAdapter(ProjectTypeJpaRepository jpaRepository, ProjectTypeJpaMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<ProjectType> findById(UUID id) {
        var projectType = mapper.toDomain(jpaRepository.findById(id)
                .orElseThrow(() -> new ProjectTypeNotFoundException(id)));
        return Optional.of(projectType);
    }
}
