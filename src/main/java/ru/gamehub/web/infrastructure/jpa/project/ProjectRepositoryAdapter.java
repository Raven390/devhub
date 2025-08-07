package ru.gamehub.web.infrastructure.jpa.project;

import jakarta.persistence.EntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectPage;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.infrastructure.jpa.project.mapper.ProjectJpaMapper;
import ru.gamehub.web.infrastructure.jpa.project.member.ProjectMemberJpaEntity;
import ru.gamehub.web.infrastructure.jpa.project.member.ProjectMemberJpaId;
import ru.gamehub.web.infrastructure.jpa.project.model.ProjectJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.role.RoleJpaEntity;
import ru.gamehub.web.infrastructure.jpa.reference.project.technology.TechnologyJpaEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Адаптер доменного репозитория проектов для интеграции с JPA-реализацией.
 * <p>
 * Реализует интерфейс {@link ProjectRepository} и инкапсулирует всю инфраструктурную логику хранения, поиска и пагинации проектов через {@link ProjectJpaRepository} и {@link ProjectJpaMapper}.
 * Обеспечивает преобразование между доменной моделью и JPA-сущностью, поддерживая чистую архитектуру (разделение домена и инфраструктуры).
 * </p>
 *
 * <b>Особенности:</b>
 * <ul>
 *   <li>Все операции над проектами (save, find, delete, пагинация) делегируются JPA-репозиторию.</li>
 *   <li>Для конвертации между слоями используется {@link ProjectJpaMapper} (в том числе для вложенных сущностей).</li>
 *   <li>Пагинация реализована через {@link org.springframework.data.domain.PageRequest} и маппинг результата в {@link ProjectPage}.</li>
 *   <li>Используется как Spring Bean (<code>@Component</code>), внедряется через DI.</li>
 * </ul>
 *
 * <b>Потокобезопасность:</b>
 * <ul>
 *   <li>Потокобезопасность определяется реализациями {@link ProjectJpaRepository} и {@link ProjectJpaMapper}.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * Project project = projectRepositoryAdapter.save(project);
 * Optional&lt;Project&gt; found = projectRepositoryAdapter.findById(projectId);
 * ProjectPage page = projectRepositoryAdapter.findPage(0, 20);
 * projectRepositoryAdapter.delete(projectId);
 * </pre>
 *
 * @see ProjectRepository
 * @see ProjectJpaRepository
 * @see ProjectJpaMapper
 * @see Project
 * @see ProjectPage
 */
@Component
public class ProjectRepositoryAdapter implements ProjectRepository {
    private final ProjectJpaRepository jpaRepository;
    private final ProjectJpaMapper projectJpaMapper;
    private final EntityManager entityManager;

    public ProjectRepositoryAdapter(ProjectJpaRepository jpaRepository, ProjectJpaMapper projectJpaMapper, EntityManager entityManager) {
        this.jpaRepository = jpaRepository;
        this.projectJpaMapper = projectJpaMapper;
        this.entityManager = entityManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Project save(Project project) {
        List<RoleJpaEntity> roles = project.getRoles().stream()
                .map(role -> entityManager.getReference(RoleJpaEntity.class, role.getId()))
                .toList();

        List<TechnologyJpaEntity> technologies = project.getTechnologies().stream()
                .map(technology -> entityManager.getReference(TechnologyJpaEntity.class, technology.getId()))
                .toList();

        List<ProjectMemberJpaEntity> members = project.getMembers().stream()
                .map(member ->
                        entityManager.getReference(ProjectMemberJpaEntity.class,
                                new ProjectMemberJpaId(member.getProjectId(), member.getUser().getId()))
                ).toList();

        ProjectJpaEntity entity = projectJpaMapper.toEntity(project);
        entity.setMembers(members);
        entity.setRoles(roles);
        entity.setTechnologies(technologies);

        jpaRepository.save(entity);
        return project;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Project> findById(UUID id) {
        return jpaRepository.findById(id).map(projectJpaMapper::toDomain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProjectPage findPage(int page, int size) {
        var resultPage = jpaRepository.findAll(PageRequest.of(page, size));
        List<Project> projects = resultPage.getContent().stream()
                .map(projectJpaMapper::toDomain)
                .toList();
        return ProjectPage.create(projects, resultPage.getTotalElements(), page, size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
