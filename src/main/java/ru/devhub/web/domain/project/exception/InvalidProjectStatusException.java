package ru.devhub.web.domain.project.exception;

import ru.devhub.web.domain.project.model.ProjectStatus;

public class InvalidProjectStatusException extends RuntimeException {
    public InvalidProjectStatusException(String message) {
        super(message);
    }

    public InvalidProjectStatusException(ProjectStatus from, ProjectStatus to) {
        super("Cannot transition project status from " + from + " to " + to);
    }
}