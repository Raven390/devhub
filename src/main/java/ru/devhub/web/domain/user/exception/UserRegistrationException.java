package ru.devhub.web.domain.user.exception;

import ru.devhub.web.application.user.register.RegisterUserService;

/**
 * Исключение, выбрасываемое при ошибках в процессе регистрации пользователя.
 * <p>
 * Используется в application- или доменном слое для сигнализации о любых ошибках, возникших при создании нового пользователя,
 * например: сбой интеграции с внешней системой (Keycloak), нарушение бизнес-ограничений, некорректные данные и т.д.
 * Обычно транслируется в HTTP 400 Bad Request или 500 Internal Server Error в зависимости от контекста.
 * </p>
 *
 * <b>Пример использования:</b>
 * <pre>
 * try {
 *     keycloakUserService.registerUser(id, email, password);
 * } catch (Exception ex) {
 *     throw new UserRegistrationException("Ошибка регистрации в Keycloak", ex);
 * }
 * </pre>
 *
 * @see RegisterUserService
 */
public class UserRegistrationException extends RuntimeException {
    public UserRegistrationException(String message) {
        super(message);
    }
    public UserRegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}


