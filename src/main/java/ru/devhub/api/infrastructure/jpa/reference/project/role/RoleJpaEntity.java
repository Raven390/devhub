package ru.devhub.api.infrastructure.jpa.reference.project.role;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-сущность для справочника ролей в проекте (таблица roles).
 * Используется в связке many-to-many с проектами (project_role) и участниками.
 */
@Entity
@Table(name = "role", schema = "devhub")
public class RoleJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    protected RoleJpaEntity() {}

    public RoleJpaEntity(String name) {
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
