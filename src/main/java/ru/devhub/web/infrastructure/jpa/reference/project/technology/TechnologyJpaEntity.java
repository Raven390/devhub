package ru.devhub.web.infrastructure.jpa.reference.project.technology;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA-сущность для справочника технологий (таблица technology).
 * Используется в связке many-to-many с проектами (project_technology).
 */
@Entity
@Table(name = "technology", schema = "devhub")
public class TechnologyJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 64)
    private String name;

    protected TechnologyJpaEntity() {}

    public TechnologyJpaEntity(String name) {
        this.name = name;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
