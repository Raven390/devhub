package ru.gamehub.web.infrastructure.jpa.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * JPA-сущность пользователя для хранения в базе данных.
 * Используется для реализации инфраструктурного слоя (интеграция с БД).
 * Связана с таблицей {@code gamehub.user}.
 */
@Entity
@Table(name = "user", schema = "gamehub")
public class UserJpaEntity {

    /**
     * Уникальный идентификатор пользователя (PK).
     */
    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    /**
     * Имя пользователя.
     */
    @Column(nullable = false, length = 200)
    private String name;

    /**
     * Email пользователя.
     * (Если есть в домене и используется — добавить в таблицу БД!)
     */
    @Column(length = 320, nullable = false)
    private String email;

    /**
     * Краткое описание (подпись) пользователя.
     */
    @Column(length = 200)
    private String headline;

    /**
     * Время создания записи.
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * Время последнего обновления записи.
     */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
    protected UserJpaEntity() {}

    public UserJpaEntity(UUID id, String name, String email, String headline) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.headline = headline;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
