package ru.devhub.api.domain.user;

import ru.devhub.api.domain.project.model.Project;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Доменная сущность пользователя платформы.
 * <p>
 * Представляет участника DevHub, который может создавать проекты, вступать в команды, управлять профилем и взаимодействовать с другими пользователями.
 * Хранит ключевые атрибуты профиля, идентификационные данные и метаданные активности.
 * </p>
 *
 * <b>Бизнес-смысл:</b>
 * <ul>
 *   <li>Каждый пользователь уникально идентифицируется по id и email.</li>
 *   <li>Может быть владельцем и/или участником одного или нескольких проектов.</li>
 *   <li>В профиле хранятся основные публичные данные (имя, headline, навыки, контакты и т.д.), которые видны другим пользователям.</li>
 *   <li>Права, роли и доступ к функциям платформы определяются не только доменом, но и внешней системой аутентификации (например, Keycloak).</li>
 * </ul>
 *
 * <b>Инварианты:</b>
 * <ul>
 *   <li>id и email уникальны и не null.</li>
 *   <li>Имя пользователя (name) не пустое.</li>
 *   <li>Профиль может содержать дополнительные валидируемые поля (headline, skills, avatar и пр.).</li>
 * </ul>
 *
 * <b>Thread-safety:</b>
 * <ul>
 *   <li>Иммутабельность зависит от реализации; рекомендуется использовать final-поля и методы только для чтения либо возвращать новые объекты при изменениях.</li>
 * </ul>
 *
 * <b>Пример создания:</b>
 * <pre>
 * User user = User.create("user@example.com", "Иван Разработчик");
 * </pre>
 *
 * <b>Жизненный цикл:</b>
 * <ul>
 *   <li>Создаётся через фабричный метод {@code create} при регистрации.</li>
 *   <li>Может быть обновлён только специальными методами (например, updateProfile).</li>
 *   <li>Удаление/архивация пользователей — отдельная бизнес-операция, требующая проверки связей.</li>
 * </ul>
 *
 * @see Project
 * @see UserRepository
 */
public class User {

    /**
     * Уникальный идентификатор пользователя.
     */
    private final UUID id;

    /**
     * Имя пользователя, отображается в интерфейсе.
     */
    private final String name;

    /**
     * Email-адрес пользователя, используется для связи и регистрации.
     */
    private final String email;

    /**
     * Краткое описание (подпись) пользователя.
     * Например: "Начинающий геймдизайнер", "Java backend-разработчик".
     */
    private final String headline;

    /**
     * Временная метка создания пользователя (устанавливается при инициализации).
     */
    private final OffsetDateTime createdAt;

    /**
     * Временная метка последнего обновления пользователя.
     * Обновляется при любом изменении данных пользователя.
     */
    private final OffsetDateTime updatedAt;

    /**
     * Приватный конструктор, вызывается только через {@link #create(String, String, String)}.
     *
     * @param name имя пользователя
     * @param email email пользователя
     * @param headline краткое описание профиля
     */
    private User(String name, String email, String headline) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.headline = headline;

        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private User(String name, String email) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.email = email;
        this.headline = null;

        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private User(UUID id, String name, String email, String headline, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.headline = headline;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    private User(UUID id) {
        this.id = id;
        this.name = null;
        this.email = null;
        this.headline = null;
        this.createdAt = null;
        this.updatedAt = null;
    }

    /**
     * Фабричный метод для создания нового пользователя.
     *
     * @param name имя пользователя
     * @param email email пользователя
     * @param headline краткое описание профиля
     * @return экземпляр пользователя
     */
    public static User create(String name, String email, String headline) {
        return new User(name, email, headline);
    }

    /**
     * Фабричный метод для создания нового пользователя.
     *
     * @param name имя пользователя
     * @param email email пользователя
     * @return экземпляр пользователя
     */
    public static User create(String name, String email) {
        return new User(name, email);
    }

    public static User create(UUID id) {
        return new User(id);
    }

    /**
     * Фабричный метод для создания пользователя из переданных параметров.
     *
     * @param name имя пользователя
     * @param email email пользователя
     * @param headline краткое описание профиля
     * @return экземпляр пользователя
     */
    public static User create(UUID id, String name, String email, String headline, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new User(id, name, email, headline, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getHeadline() {
        return headline;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
