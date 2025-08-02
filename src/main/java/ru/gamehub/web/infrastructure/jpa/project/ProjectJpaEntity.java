package ru.gamehub.web.infrastructure.jpa.project;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import ru.gamehub.web.infrastructure.jpa.user.UserJpaEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * JPA-сущность для хранения в БД
 */
@Entity
@Table(name = "project", schema = "gamehub")
public class ProjectJpaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserJpaEntity owner;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 700)
    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected ProjectJpaEntity() {} // JPA требует пустой конструктор

    public ProjectJpaEntity(UUID id, UserJpaEntity owner, String name, String description, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserJpaEntity getOwner() {
        return owner;
    }

    public void setOwner(UserJpaEntity owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

