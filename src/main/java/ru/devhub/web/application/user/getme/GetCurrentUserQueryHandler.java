package ru.devhub.web.application.user.getme;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.domain.user.User;
import ru.devhub.web.domain.user.UserRepository;
import ru.devhub.web.domain.user.exception.UserNotFoundException;

/**
 * Обработчик запроса {@link GetCurrentUserQuery}.
 *
 * <p>Возвращает полный профиль текущего аутентифицированного пользователя.
 * Если пользователь не найден (например, JWT валиден, но запись в БД отсутствует) —
 * бросает {@link UserNotFoundException}, которое маппится в 404.</p>
 */
@Service
@Transactional(readOnly = true)
public class GetCurrentUserQueryHandler {

    private final UserRepository userRepository;

    public GetCurrentUserQueryHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User handle(GetCurrentUserQuery query) {
        return userRepository.findById(query.userId())
                .orElseThrow(() -> new UserNotFoundException(query.userId()));
    }
}
