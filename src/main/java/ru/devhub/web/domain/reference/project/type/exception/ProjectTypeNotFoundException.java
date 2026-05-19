package ru.devhub.web.domain.reference.project.type.exception;

import java.util.UUID;

public class ProjectTypeNotFoundException extends RuntimeException {
    public ProjectTypeNotFoundException(UUID id) {
        super("ProjectType with id: %s not found".formatted(id));
    }
}
