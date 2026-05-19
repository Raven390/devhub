package ru.devhub.web.domain.project.exception;

import java.util.UUID;

public class ProjectMemberNotFoundException extends RuntimeException {
    public ProjectMemberNotFoundException(UUID projectId, UUID memberId) {
        super("Project member not found. ProjectId: %s, memberId: %s".formatted(projectId, memberId));
    }
}
