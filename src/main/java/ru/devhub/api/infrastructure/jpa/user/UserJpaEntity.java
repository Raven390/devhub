package ru.devhub.api.infrastructure.jpa.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import ru.devhub.api.domain.user.User;

import java.time.OffsetDateTime;
import java.util.UUID;


/**
 * JPA-сущность пользователя для хранения в базе данных.
 * <p>
 * Маппит таблицу {@code gamehub.user} и отражает структуру данных пользователя на инфраструктурном уровне.
 * Используется только для хранения и извлечения данных, не содержит бизнес-логики.
 * Является слоем адаптации между доменной моделью пользователя и СУБД.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>id</b> — уникальный идентификатор пользователя (PRIMARY KEY, UUID), не изменяется после создания.</li>
 *   <li><b>name</b> — имя пользователя, не null, max 200 символов.</li>
 *   <li><b>email</b> — адрес электронной почты пользователя, не null, max 320 символов (уникальность должна обеспечиваться на уровне БД/репозитория).</li>
 *   <li><b>headline</b> — краткое описание (подпись, статус), max 200 символов, может быть null.</li>
 *   <li><b>createdAt</b> — время создания записи, не null.</li>
 *   <li><b>updatedAt</b> — время последнего обновления записи, не null.</li>
 * </ul>
 *
 * <b>Ограничения:</b>
 * <ul>
 *   <li>Поля {@code id}, {@code name}, {@code email}, {@code createdAt}, {@code updatedAt} должны быть заполнены для валидного объекта.</li>
 *   <li>Для email рекомендуется обеспечить уникальный индекс в базе данных.</li>
 *   <li>Для работы JPA требуется пустой защищённый конструктор.</li>
 * </ul>
 *
 * <b>Применение:</b>
 * <ul>
 *   <li>Используется только в инфраструктурном слое приложения и в репозиториях, не протекает в доменную/аппликационную логику.</li>
 * </ul>
 *
 * @see User
 * @see UserJpaMapper
 */
@Entity
@Table(name = "users", schema = "devhub")
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
