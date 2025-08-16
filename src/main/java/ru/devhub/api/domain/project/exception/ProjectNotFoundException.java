package ru.devhub.api.domain.project.exception;

import ru.devhub.api.application.project.update.UpdateProjectService;

import java.util.UUID;

/**
 * Исключение, выбрасываемое при попытке обращения к несуществующему проекту.
 * <p>
 * Используется в application- или доменном слое для сигнализации, что проект с указанным идентификатором не найден.
 * Обычно транслируется в HTTP 404 Not Found на уровне REST API.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>id</b> — идентификатор проекта, который не найден.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * Project project = projectRepository.findById(id)
 *     .orElseThrow(() -> new ProjectNotFoundException(id));
 * </pre>
 *
 * @see UpdateProjectService
 */
public class ProjectNotFoundException extends RuntimeException {
    public ProjectNotFoundException(UUID id) {
        super("Project not found by id: %s".formatted(id));
    }
}
