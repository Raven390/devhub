package ru.gamehub.web.domain.user;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Доменная сущность пользователя платформы.
 * Представляет участника, который может создавать проекты, присоединяться к командам и взаимодействовать с другими участниками.
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
     * @param id бизнес идентификатор пользователя
     * @param name имя пользователя
     * @param email email пользователя
     * @return экземпляр пользователя
     */
    public static User create(String name, String email) {
        return new User(name, email);
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
