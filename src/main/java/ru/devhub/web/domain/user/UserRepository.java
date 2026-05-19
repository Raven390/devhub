package ru.devhub.web.domain.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для доступа к пользователям.
 * <p>
 * Абстрагирует источник хранения (БД, кэш, память и пр.) и предоставляет CRUD-операции для доменной сущности {@link User}.
 * Используется в application- и доменном слоях для получения, сохранения, поиска и удаления пользователей.
 * </p>
 *
 * <b>Особенности и ограничения:</b>
 * <ul>
 *   <li>Гарантирует уникальность пользователя по id и email.</li>
 *   <li>Может иметь разные реализации — например, на основе JPA, памяти (для тестов), внешних сервисов.</li>
 *   <li>В случае обновления существующего пользователя метод {@code save} обязан заменить все изменённые поля.</li>
 *   <li>Вызов {@code findAll} для больших данных может быть неэффективен — рекомендуется использовать пагинацию при расширении.</li>
 *   <li>Потокобезопасность зависит от реализации.</li>
 * </ul>
 *
 * <b>Типовые сценарии использования:</b>
 * <pre>
 * // Получить пользователя по id
 * userRepository.findById(userId)
 *     .orElseThrow(() -> new UserNotFoundException(userId));
 *
 * // Проверить уникальность email при регистрации
 * if (userRepository.findByEmail(email).isPresent()) {
 *     throw new UserAlreadyExistsException(email);
 * }
 *
 * // Сохранить или обновить пользователя
 * userRepository.save(user);
 *
 * // Удалить пользователя
 * userRepository.delete(userId);
 * </pre>
 *
 * <b>Расширение:</b>
 * <ul>
 *   <li>При необходимости поддержки поиска по другим атрибутам (username, роли) — добавить соответствующие методы.</li>
 *   <li>Для оптимизации и масштабирования добавить методы с пагинацией или фильтрацией.</li>
 * </ul>
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

    List<User> findAllById(List<UUID> userIdList);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор удаляемого пользователя
     */
    void delete(UUID id);


    List<User> searchByNameOrEmail(String query, int limit);

    long count();
}
