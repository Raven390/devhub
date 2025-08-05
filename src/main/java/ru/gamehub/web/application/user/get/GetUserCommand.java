package ru.gamehub.web.application.user.get;

import ru.gamehub.web.application.common.Command;

import java.util.UUID;

/**
 * Команда для получения информации о пользователе по идентификатору.
 * <p>
 * Используется в application-слое (DDD, CQRS) как DTO между контроллером и сервисом получения пользователя.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>id</b> — уникальный идентификатор пользователя (не null).</li>
 * </ul>
 *
 * <b>Ограничения:</b>
 * <ul>
 *   <li>Валидация корректности id (например, существует ли пользователь) происходит на уровне сервиса или домена.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * GetUserCommand cmd = new GetUserCommand(userId);
 * User user = getUserService.handle(cmd);
 * </pre>
 *
 * @see ru.gamehub.web.application.user.get.GetUserService
 */
public record GetUserCommand(UUID id) implements Command {
}
