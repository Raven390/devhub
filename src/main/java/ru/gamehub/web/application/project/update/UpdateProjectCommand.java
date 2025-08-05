package ru.gamehub.web.application.project.update;

import ru.gamehub.web.application.common.Command;

import java.util.UUID;

/**
 * Команда для обновления информации о проекте.
 * <p>
 * Используется в application-слое (DDD, CQRS) для передачи параметров обновления проекта сервису.
 * Гарантирует, что обновление выполняется только владельцем проекта.
 * </p>
 *
 * <p>
 * <b>Поля:</b>
 * <ul>
 *   <li><b>projectId</b> — идентификатор проекта для обновления (не null).</li>
 *   <li><b>ownerId</b> — идентификатор пользователя-инициатора (используется для проверки прав).</li>
 *   <li><b>name</b> — новое название проекта (не пустое, валидация на уровне бизнес-логики).</li>
 *   <li><b>description</b> — новое описание (может быть пустым).</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Ограничения:</b>
 * <ul>
 *   <li>Только владелец (ownerId) может инициировать изменение.</li>
 *   <li>Имя проекта не должно быть пустым и превышать лимит длины.</li>
 * </ul>
 * </p>
 *
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * UpdateProjectCommand cmd = new UpdateProjectCommand(
 *     projectId, ownerId, "Новое имя", "Новое описание"
 * );
 * updateProjectService.handle(cmd);
 * </pre>
 * </p>
 *
 * @see ru.gamehub.web.application.project.update.UpdateProjectService
 */
public record UpdateProjectCommand(
        UUID projectId,
        UUID ownerId,
        String name,
        String description
) implements Command {}
