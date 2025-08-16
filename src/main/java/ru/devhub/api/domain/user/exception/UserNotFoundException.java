package ru.devhub.api.domain.user.exception;

import ru.devhub.api.application.user.get.GetUserService;

import java.util.List;
import java.util.UUID;

/**
 * Исключение, выбрасываемое при попытке обращения к несуществующему пользователю по идентификатору.
 * <p>
 * Используется в application- или доменном слое для сигнализации о том, что пользователь с указанным id не найден.
 * Обычно транслируется в HTTP 404 Not Found на уровне REST API.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>id</b> — идентификатор пользователя, который не найден.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * User user = userRepository.findById(id)
 *     .orElseThrow(() -> new UserNotFoundException(id));
 * </pre>
 *
 * @see GetUserService
 */
public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(List<UUID> idList) {
        super("Users with id: %s not found".formatted(idList));
    }

    public UserNotFoundException(UUID id) {
        super("User not found by id: %s".formatted(id));
    }
}
