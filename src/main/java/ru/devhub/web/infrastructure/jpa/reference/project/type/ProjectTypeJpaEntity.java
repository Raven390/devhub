package ru.devhub.web.infrastructure.jpa.reference.project.type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "project_type", schema = "devhub")
public class ProjectTypeJpaEntity {
    @Id
    private UUID id;

    @Column(nullable = false, length = 64, unique = true)
    private String name;

    public ProjectTypeJpaEntity() {
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
}
