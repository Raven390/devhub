package ru.devhub.api.domain.reference.project.type;

import java.util.UUID;

public class ProjectType {
    private final UUID id;
    private final String name;

    private ProjectType(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public static ProjectType create(UUID id, String name) {
        return new ProjectType(id, name);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
