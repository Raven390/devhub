package ru.devhub.api.domain.project.exception;

import ru.devhub.api.application.project.update.UpdateProjectService;

import java.util.UUID;

/**
 * Исключение, выбрасываемое при попытке неавторизованного доступа к проекту.
 * <p>
 * Используется в доменной или application-логике для сигнализации о том, что операция над проектом
 * попытался выполнить пользователь, не являющийся владельцем (или без необходимых прав).
 * Обычно приводит к возврату HTTP 403 Forbidden на уровне API.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>ownerId</b> — идентификатор владельца проекта.</li>
 *   <li><b>userId</b> — идентификатор пользователя, совершившего попытку доступа.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * if (!project.getOwner().getId().equals(userId)) {
 *     throw new ProjectAccessDeniedException(project.getOwner().getId(), userId);
 * }
 * </pre>
 *
 * @see UpdateProjectService
 */
public class ProjectAccessDeniedException extends RuntimeException {
    public ProjectAccessDeniedException(UUID ownerId, UUID userId) {
        super("Project access denied. OwnerId: %s, userId: %s".formatted(ownerId, userId));
    }
}

