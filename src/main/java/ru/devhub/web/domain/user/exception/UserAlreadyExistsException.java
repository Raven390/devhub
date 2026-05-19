package ru.devhub.web.domain.user.exception;

import ru.devhub.web.application.user.register.RegisterUserService;

/**
 * Исключение, выбрасываемое при попытке регистрации пользователя с уже существующим email.
 * <p>
 * Используется в application- или доменном слое для сигнализации о нарушении уникальности email при регистрации.
 * Обычно приводит к возврату HTTP 409 Conflict на уровне REST API.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>email</b> — адрес электронной почты, по которому уже зарегистрирован пользователь.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * if (userRepository.findByEmail(email).isPresent()) {
 *     throw new UserAlreadyExistsException(email);
 * }
 * </pre>
 *
 * @see RegisterUserService
 */
public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String email) {
        super("User with email %s already exists".formatted(email));
    }
}

