package ru.devhub.web.application.user.register;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.domain.user.exception.UserAlreadyExistsException;
import ru.devhub.web.domain.user.exception.UserRegistrationException;
import ru.devhub.web.infrastructure.security.KeycloakUserService;

/**
 * Application-сервис для регистрации нового пользователя.
 *
 * <p>При создании пользователя автоматически генерирует {@code avatarUrl} на основе
 * email через Gravatar (MD5 hash). Это обеспечивает наличие аватара с первого входа,
 * без необходимости загружать изображение вручную.</p>
 */
@Service
public class RegisterUserService {

    private static final String GRAVATAR_BASE = "https://www.gravatar.com/avatar/";
    private static final String GRAVATAR_PARAMS = "?d=identicon&s=200";

    private final UserRepository userRepository;
    private final KeycloakUserService keycloakUserService;

    public RegisterUserService(UserRepository userRepository, KeycloakUserService keycloakUserService) {
        this.userRepository = userRepository;
        this.keycloakUserService = keycloakUserService;
    }

    /**
     * Регистрирует нового пользователя: создаёт запись в БД и учётную запись в Keycloak.
     *
     * @param cmd параметры нового пользователя (email, имя, пароль)
     * @return созданный {@link User} с заполненным {@code avatarUrl}
     * @throws UserAlreadyExistsException если email уже зарегистрирован
     * @throws UserRegistrationException  если регистрация в Keycloak завершилась ошибкой
     */
    @Transactional
    public User handle(RegisterUserCommand cmd) {
        String email = cmd.email();
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException(email);
        }

        String avatarUrl = resolveAvatarUrl(email);
        User user = User.create(cmd.name(), email, avatarUrl);
        user = userRepository.save(user);

        keycloakUserService.registerUser(user.getId(), user.getEmail(), cmd.password());
        return user;
    }

    /**
     * Строит детерминированный Gravatar URL для email.
     * Fallback {@code ?d=identicon} генерирует уникальный геометрический аватар если
     * у пользователя нет аккаунта Gravatar.
     */
    private String resolveAvatarUrl(String email) {
        String normalized = email.trim().toLowerCase();
        String hash = DigestUtils.md5DigestAsHex(normalized.getBytes());
        return GRAVATAR_BASE + hash + GRAVATAR_PARAMS;
    }
}
