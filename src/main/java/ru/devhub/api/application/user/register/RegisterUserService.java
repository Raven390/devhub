package ru.devhub.api.application.user.register;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.api.domain.user.User;
import ru.devhub.api.domain.user.UserRepository;
import ru.devhub.api.domain.user.exception.UserAlreadyExistsException;
import ru.devhub.api.domain.user.exception.UserRegistrationException;
import ru.devhub.api.infrastructure.security.KeycloakUserService;

/**
 * Application-сервис для регистрации нового пользователя.
 * <p>
 * Инкапсулирует бизнес-логику регистрации, интеграцию с внешней системой аутентификации (Keycloak) и управление доменной сущностью пользователя.
 * Реализует транзакционный сценарий создания нового пользователя и обеспечения уникальности email.
 * </p>
 *
 * <b>Паттерны:</b> Application Service (DDD), интеграция с внешними сервисами через инфраструктурный слой.
 * <p>
 * <b>Side effects:</b>
 * <ul>
 *   <li>Создаёт новую запись в {@link UserRepository}.</li>
 *   <li>Выполняет регистрацию в Keycloak через {@link KeycloakUserService} (создаёт пользователя и устанавливает пароль).</li>
 * </ul>
 *
 * <b>Валидация и безопасность:</b>
 * <ul>
 *   <li>Гарантирует уникальность email на уровне приложения (но рекомендуется дополнительная уникальность на уровне БД).</li>
 *   <li>Пароль не хранится в домене — передаётся только во внешний сервис аутентификации.</li>
 *   <li>Вся операция атомарна (аннотирована {@link org.springframework.transaction.annotation.Transactional}).</li>
 * </ul>
 *
 * <b>Исключения:</b>
 * <ul>
 *   <li>{@link UserAlreadyExistsException} — если пользователь с таким email уже существует.</li>
 *   <li>{@link UserRegistrationException} — если произошла ошибка при регистрации в Keycloak.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * User user = registerUserService.handle(
 *     new RegisterUserCommand("user@example.com", "Имя", "Пароль123")
 * );
 * </pre>
 *
 * @see RegisterUserCommand
 * @see UserRepository
 * @see KeycloakUserService
 */
@Service
public class RegisterUserService {

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    public RegisterUserService(UserRepository userRepository, KeycloakUserService keycloakUserService) {
        this.userRepository = userRepository;
        this.keycloakUserService = keycloakUserService;
    }

    /**
     * Регистрирует нового пользователя, создавая запись в системе и в Keycloak.
     *
     * @param cmd параметры нового пользователя (email, имя, пароль)
     * @return созданный {@link User}
     * @throws UserAlreadyExistsException если email уже зарегистрирован
     * @throws UserRegistrationException если регистрация в Keycloak завершилась ошибкой
     */
    @Transactional
    public User handle(RegisterUserCommand cmd) throws UserAlreadyExistsException, UserRegistrationException {
        String email = cmd.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }
        // Создаем доменный объект
        User user = User.create(cmd.name(), cmd.email());
        user = userRepository.save(user);

        // Регистрируем в Keycloak
        keycloakUserService.registerUser(user.getId(), user.getEmail(), cmd.password());
        return user;
    }
}
