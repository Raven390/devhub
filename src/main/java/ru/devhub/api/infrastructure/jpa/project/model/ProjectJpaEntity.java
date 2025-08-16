package ru.devhub.api.infrastructure.jpa.project.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import ru.devhub.api.domain.project.Project;
import ru.devhub.api.infrastructure.jpa.project.ProjectJpaRepository;
import ru.devhub.api.infrastructure.jpa.project.member.ProjectMemberJpaEntity;
import ru.devhub.api.infrastructure.jpa.reference.project.role.RoleJpaEntity;
import ru.devhub.api.infrastructure.jpa.reference.project.technology.TechnologyJpaEntity;
import ru.devhub.api.infrastructure.jpa.reference.project.type.ProjectTypeJpaEntity;
import ru.devhub.api.infrastructure.jpa.user.UserJpaEntity;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA-сущность для хранения проектов в реляционной базе данных.
 * <p>
 * Представляет таблицу <b>gamehub.project</b> и служит инфраструктурным отображением доменной сущности проекта для ORM-слоя.
 * Используется только для хранения, извлечения и маппинга данных между БД и доменной моделью.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>id</b> — уникальный идентификатор проекта (PRIMARY KEY, UUID).</li>
 *   <li><b>owner</b> — внешний ключ на {@link UserJpaEntity}, обязательно не null (FK: owner_id).</li>
 *   <li><b>name</b> — название проекта, не null, max 200 символов.</li>
 *   <li><b>description</b> — описание проекта, не null, max 700 символов.</li>
 *   <li><b>createdAt</b> — время создания записи (автоматически задаётся при создании).</li>
 *   <li><b>updatedAt</b> — время последнего обновления записи.</li>
 * </ul>
 *
 * <b>Ограничения и бизнес-правила:</b>
 * <ul>
 *   <li>Владелец (owner) должен существовать в системе.</li>
 *   <li>Названия и описания валидируются на уровне length и NOT NULL через аннотации.</li>
 *   <li>Рекомендуется использовать только через Spring Data репозиторий для абстракции бизнес-логики.</li>
 * </ul>
 *
 * <b>Примечание по реализации:</b>
 * <ul>
 *   <li>Сущность не должна содержать бизнес-логику — только геттеры, сеттеры, конструкторы.</li>
 *   <li>Для работы с JPA требуется пустой защищённый конструктор.</li>
 *   <li>Отношение с владельцем реализовано через <b>@ManyToOne(fetch = LAZY)</b>.</li>
 * </ul>
 *
 * @see UserJpaEntity
 * @see Project
 * @see ProjectJpaRepository
 */
@Entity
@Table(name = "project", schema = "devhub")
public class ProjectJpaEntity {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserJpaEntity owner;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(name = "short_description", nullable = false, length = 300)
    private String shortDescription;

    @Column(nullable = false, length = 3000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_id", nullable = false)
    private ProjectTypeJpaEntity type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatusJpaEnum status;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "devhub",
            name = "project_technology",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private List<TechnologyJpaEntity> technologies = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "devhub",
            name = "project_role",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<RoleJpaEntity> roles = new ArrayList<>();

    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMemberJpaEntity> members = new ArrayList<>();


    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected ProjectJpaEntity() {}

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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProjectTypeJpaEntity getType() {
        return type;
    }

    public void setType(ProjectTypeJpaEntity type) {
        this.type = type;
    }

    public ProjectStatusJpaEnum getStatus() {
        return status;
    }

    public void setStatus(ProjectStatusJpaEnum status) {
        this.status = status;
    }

    public List<TechnologyJpaEntity> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(List<TechnologyJpaEntity> technologies) {
        this.technologies = technologies;
    }

    public List<RoleJpaEntity> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleJpaEntity> roles) {
        this.roles = roles;
    }

    public List<ProjectMemberJpaEntity> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMemberJpaEntity> members) {
        this.members = members;
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

