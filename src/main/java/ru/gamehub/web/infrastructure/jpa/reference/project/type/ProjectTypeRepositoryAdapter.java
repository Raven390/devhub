package ru.gamehub.web.infrastructure.jpa.reference.project.type;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.domain.reference.project.type.ProjectTypePage;
import ru.gamehub.web.domain.reference.project.type.ProjectTypeRepository;
import ru.gamehub.web.domain.reference.project.type.exception.ProjectTypeNotFoundException;
import ru.gamehub.web.infrastructure.jpa.reference.project.type.mapper.ProjectTypeJpaMapper;

import java.util.List;
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

    @Override
    public ProjectType save(ProjectType projectType) {
        var entity = mapper.toEntity(projectType);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectTypePage findPage() {
        List<ProjectTypeJpaEntity> resultPage = jpaRepository.findAll();
        List<ProjectType> projectTypes = resultPage.stream()
                .map(mapper::toDomain)
                .toList();
        return ProjectTypePage.create(projectTypes, projectTypes.size(), 0, projectTypes.size());
    }
}
