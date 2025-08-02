package ru.gamehub.web.domain.project;

import ru.gamehub.web.domain.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Доменная сущность проекта. Представляет собой команду или инициативу,
 * созданную пользователем на платформе для совместной разработки.
 */
public class Project {
    /**
     * Уникальный идентификатор проекта.
     */
    private final UUID id;

    /**
     * Пользователь, создавший проект.
     * Является владельцем проекта и может управлять им.
     */
    private final User owner;

    /**
     * Название проекта, отображаемое в списках и карточках.
     */
    private final String name;

    /**
     * Краткое описание проекта, отображается на странице проекта.
     */
    private final String description;

    /**
     * Временная метка создания проекта (устанавливается при инициализации).
     */
    private final OffsetDateTime createdAt;

    /**
     * Временная метка последнего обновления проекта.
     * Обновляется при любом изменении данных проекта.
     */
    private final OffsetDateTime updatedAt;

    private Project(User owner, String name, String description) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.name = name;
        this.description = description;

        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private Project(UUID id, User owner, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Создаёт новый проект с текущим временем создания и модификации.
     *
     * @param owner Владелец проекта
     * @param name Название проекта
     * @param description Описание проекта
     * @return Новый экземпляр проекта
     */
    public static Project create(User owner, String name, String description) {
        return new Project(owner, name, description);
    }

    /**
     * Доменный объект из переданных параметров
     *
     * @param owner Владелец проекта
     * @param name Название проекта
     * @param description Описание проекта
     * @return Новый экземпляр проекта
     */
    public static Project create(UUID id, User owner, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Project(id, owner, name, description, createdAt, updatedAt);
    }

    public UUID getId() {
        return id;
    }

    public User getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
