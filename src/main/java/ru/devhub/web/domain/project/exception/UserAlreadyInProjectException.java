package ru.devhub.web.domain.project.exception;

import java.util.UUID;

public class UserAlreadyInProjectException extends RuntimeException {
    public UserAlreadyInProjectException(UUID userId, UUID projectId) {
        super("User %s already has an active membership in project %s".formatted(userId, projectId));
    }
}
