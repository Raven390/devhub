package ru.gamehub.web.infrastructure.jpa.project;

import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class ProjectRepositoryAdapter implements ProjectRepository {
    private final JpaProjectRepository jpaRepository;
    private final ProjectJpaMapper projectJpaMapper;

    public ProjectRepositoryAdapter(JpaProjectRepository jpaRepository, ProjectJpaMapper projectJpaMapper) {
        this.jpaRepository = jpaRepository;
        this.projectJpaMapper = projectJpaMapper;
    }

    @Override
    public Project save(Project project) {
        ProjectJpaEntity entity = projectJpaMapper.toEntity(project);
        entity = jpaRepository.save(entity);
        return projectJpaMapper.toDomain(entity);
    }

    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(projectJpaMapper::toDomain);
    }

    @Override
    public List<Project> findAll() {
        return jpaRepository.findAll().stream()
                .map(projectJpaMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
