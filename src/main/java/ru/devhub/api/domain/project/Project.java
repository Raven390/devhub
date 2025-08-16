package ru.devhub.api.domain.project;

import ru.devhub.api.domain.project.member.ProjectMember;
import ru.devhub.api.domain.reference.project.role.Role;
import ru.devhub.api.domain.reference.project.technology.Technology;
import ru.devhub.api.domain.reference.project.type.ProjectType;
import ru.devhub.api.domain.user.User;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Доменная сущность "Проект".
 * <p>
 * Представляет инициативу или команду, созданную пользователем для совместной разработки на платформе.
 * Инкапсулирует ключевые атрибуты и базовые бизнес-правила проекта.
 * </p>
 *
 * <b>Бизнес-смысл:</b>
 * <ul>
 *   <li>Каждый проект принадлежит конкретному пользователю-владельцу.</li>
 *   <li>Может быть создан только с валидными названием и описанием (валидация рекомендуется в домене или application-слое).</li>
 *   <li>Изменение данных проекта осуществляется только через методы <code>update()</code> или фабричные методы <code>create()</code> (иммутабельность).</li>
 *   <li>Дата создания и обновления фиксируются автоматически при создании/изменении.</li>
 * </ul>
 *
 * <b>Инварианты:</b>
 * <ul>
 *   <li>id — уникален для каждого проекта.</li>
 *   <li>owner — не null (каждый проект должен иметь владельца).</li>
 *   <li>name — не пустое, max длина определяется бизнес-правилами платформы.</li>
 * </ul>
 *
 * <b>Потокобезопасность:</b>
 * <ul>
 *   <li>Иммутабельный объект: все поля final, все изменения через возвращение нового экземпляра.</li>
 * </ul>
 *
 * <b>Пример создания:</b>
 * <pre>
 * Project project = Project.create(owner, "GameHub", "Платформа для командной разработки");
 * </pre>
 *
 * @see User
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

    private final String shortDescription;

    private final ProjectType type;

    private final ProjectStatus status;

    private final List<Technology> technologies;

    private final List<Role> roles;

    private final List<ProjectMember> members;
    /**
     * Временная метка создания проекта (устанавливается при инициализации).
     */
    private final OffsetDateTime createdAt;

    /**
     * Временная метка последнего обновления проекта.
     * Обновляется при любом изменении данных проекта.
     */
    private final OffsetDateTime updatedAt;

    public static Builder builder() {
        return new Builder();
    }

    private Project(Builder builder) {
        this.id = builder.id;
        this.owner = builder.owner;
        this.name = builder.name;
        this.description = builder.description;
        this.shortDescription = builder.shortDescription;
        this.type = builder.type;
        this.status = builder.status;
        this.technologies = builder.technologies;
        this.roles = builder.roles;
        this.members = builder.members;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.createdAt;
    }

    public static class Builder {
        private UUID id;
        private User owner;
        private String name;
        private String description;
        private String shortDescription;
        private ProjectType type;
        private ProjectStatus status;
        private List<Technology> technologies;
        private List<Role> roles;
        private List<ProjectMember> members;
        private OffsetDateTime createdAt;
        private OffsetDateTime updatedAt;

        /**
         * Копирует все поля из существующего Project для быстрого клонирования/апдейта.
         */
        public Builder from(Project existing) {
            this.id = existing.getId();
            this.owner = existing.getOwner();
            this.name = existing.getName();
            this.description = existing.getDescription();
            this.shortDescription = existing.getShortDescription();
            this.type = existing.getType();
            this.status = existing.getStatus();
            this.technologies = existing.getTechnologies();
            this.roles = existing.getRoles();
            this.members = existing.getMembers();
            this.createdAt = existing.getCreatedAt();
            return this;
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }
        public Builder owner(User owner) {
            this.owner = owner;
            return this;
        }
        public Builder name(String name) {
            this.name = name;
            return this;
        }
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        public Builder shortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }
        public Builder type(ProjectType type) {
            this.type = type;
            return this;
        }
        public Builder status(ProjectStatus status) {
            this.status = status;
            return this;
        }
        public Builder technologies(List<Technology> technologies) {
            this.technologies = technologies;
            return this;
        }
        public Builder roles(List<Role> roles) {
            this.roles = roles;
            return this;
        }
        public Builder members(List<ProjectMember> members) {
            this.members = members;
            return this;
        }
        public Builder createdAt(OffsetDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Project build() {
            // Пример простых доменных инвариантов (можешь расширять)
            if (id == null) id = UUID.randomUUID();
            if (owner == null) throw new IllegalArgumentException("Owner is required");
            if (name == null || name.isBlank()) throw new IllegalArgumentException("Name is required");
            if (createdAt == null && updatedAt == null) {
                createdAt = OffsetDateTime.now();
                updatedAt = createdAt;
            }

            if (updatedAt == null) {
                updatedAt = OffsetDateTime.now();
            }

            return new Project(this);
        }
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

    public String getShortDescription() {
        return shortDescription;
    }

    public ProjectType getType() {
        return type;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public List<Technology> getTechnologies() {
        return technologies;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
