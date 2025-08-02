package ru.gamehub.web.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к пользователям.
 * Интерфейс абстрагирует источник хранения (БД, память и т.д.)
 * и предоставляет операции для работы с доменной сущностью {@link User}.
 */
public interface UserRepository {

    /**
     * Сохраняет пользователя в хранилище.
     * Если пользователь уже существует, он должен быть обновлён.
     *
     * @param user пользователь для сохранения
     * @return сохранённый пользователь
     */
    User save(User user);

    /**
     * Ищет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return Optional с найденным пользователем или пустой, если не найден
     */
    Optional<User> findById(UUID id);

    /**
     * Ищет пользователя по email.
     *
     * @param email email пользователя
     * @return Optional с найденным пользователем или пустой, если не найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Возвращает всех пользователей из хранилища.
     *
     * @return список всех пользователей
     */
    List<User> findAll();

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор удаляемого пользователя
     */
    void delete(UUID id);
}
