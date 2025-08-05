package ru.gamehub.web.domain.project;

import ru.gamehub.web.domain.project.member.ProjectMember;
import ru.gamehub.web.domain.project.role.Role;
import ru.gamehub.web.domain.project.technology.Technology;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.domain.user.User;

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
 * @see ru.gamehub.web.domain.user.User
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

    private Project(User owner, String name, String description, String shortDescription, ProjectType type,
                    ProjectStatus status, List<Technology> technologies, List<Role> roles, List<ProjectMember> members) {
        this.shortDescription = shortDescription;
        this.type = type;
        this.status = status;
        this.technologies = technologies;
        this.roles = roles;
        this.members = members;
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.name = name;
        this.description = description;

        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    private Project(UUID id, User owner, String name, String description, String shortDescription,
                    ProjectType type, ProjectStatus status, List<Technology> technologies,
                    List<Role> roles, List<ProjectMember> members,
                    OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id;
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.type = type;
        this.status = status;
        this.technologies = technologies;
        this.roles = roles;
        this.members = members;
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
    public static Project create(User owner, String name, String description, String shortDescription,
                                 ProjectType type, ProjectStatus status, List<Technology> technologies,
                                 List<Role> roles, List<ProjectMember> members) {
        return new Project(owner, name, description, shortDescription,
                type, status, technologies, roles, members);
    }

    public Project update(String name, String description, String shortDescription, ProjectType type,
                          ProjectStatus status, List<Technology> technologies,
                          List<Role> roles, List<ProjectMember> members) {
        return new Project(this.id, this.owner, name, description, shortDescription,
                type, status, technologies, roles, members, this.createdAt, this.updatedAt);
    }

    /**
     * Доменный объект из переданных параметров
     *
     * @param owner Владелец проекта
     * @param name Название проекта
     * @param description Описание проекта
     * @return Новый экземпляр проекта
     */
    public static Project create(UUID id, User owner, String name,
                                 String description, String shortDescription, ProjectType type,
                                 ProjectStatus status, List<Technology> technologies, List<Role> roles,
                                 List<ProjectMember> members, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        return new Project(id, owner, name, description, shortDescription,
                type, status, technologies, roles, members, createdAt, updatedAt);
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
