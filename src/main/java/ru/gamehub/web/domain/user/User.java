package ru.gamehub.web.domain.user;

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
}
