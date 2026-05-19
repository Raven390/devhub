package ru.devhub.web.domain.project.repository;

import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.model.ProjectPage;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к проектам.
 * Интерфейс абстрагирует источник хранения (БД, память и т.д.)
 * и предоставляет операции для работы с доменной сущностью {@link Project}.
 */
public interface ProjectRepository {

    /**
     * Сохраняет проект в хранилище.
     * Если проект уже существует, он должен быть обновлён.
     *
     * @param project проект для сохранения
     */
    Project save(Project project);

    /**
     * Ищет проект по его идентификатору.
     *
     * @param id идентификатор проекта
     * @return Optional с найденным проектом или пустой, если не найден
     */
    Optional<Project> findById(UUID id);

    /**
     * Возвращает страницу проектов с учетом фильтрации.
     */
    ProjectPage findPage(ru.devhub.web.application.project.query.list.ListProjectsQuery query);

    /**
     * Удаляет проект по его идентификатору.
     *
     * @param id идентификатор удаляемого проекта
     */
    void delete(UUID id);

    long countByStatusIn(java.util.List<ru.devhub.web.domain.project.model.ProjectStatus> statuses);

    long countByStatus(ru.devhub.web.domain.project.model.ProjectStatus status);
}