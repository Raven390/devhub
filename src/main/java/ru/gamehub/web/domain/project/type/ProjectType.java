package ru.gamehub.web.domain.project.type;

import java.util.UUID;

public class ProjectType {
    private final UUID id;
    private final String name;

    public ProjectType(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProjectType create(UUID id, String name) {
        return new ProjectType(id, name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
