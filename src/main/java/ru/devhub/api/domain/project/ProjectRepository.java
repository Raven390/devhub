package ru.devhub.api.domain.project;

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
     * Возвращает все проекты из хранилища.
     *
     * @return список всех проектов
     */
    ProjectPage findPage(int page, int size);

    /**
     * Удаляет проект по его идентификатору.
     *
     * @param id идентификатор удаляемого проекта
     */
    void delete(UUID id);
}
