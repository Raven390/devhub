package ru.devhub.web.application.user.get;

import org.springframework.stereotype.Service;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;

import java.util.Optional;

/**
 * Application-сервис для получения пользователя по идентификатору.
 * <p>
 * Используется в application-слое (DDD, CQRS) для поиска пользователя по id.
 * Делегирует получение сущности {@link User} доменному репозиторию.
 * </p>
 *
 * <b>Side effects:</b> Только чтение, не изменяет состояние системы.<br>
 * <b>Thread-safety:</b> Потокобезопасность зависит от реализации {@link UserRepository}.
 * <p>
 * <b>Пример использования:</b>
 * <pre>
 * Optional&lt;User&gt; user = getUserService.handle(new GetUserCommand(userId));
 * </pre>
 *
 * @see GetUserCommand
 * @see UserRepository
 */
@Service
public class GetUserService {
    private final UserRepository userRepository;

    public GetUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Возвращает пользователя по идентификатору.
     *
     * @param cmd команда с id пользователя
     * @return {@link Optional} пользователя, если найден
     */
    public Optional<User> handle(GetUserCommand cmd) {
        return userRepository.findById(cmd.id());
    }
}