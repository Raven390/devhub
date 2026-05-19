package ru.devhub.web.domain.project.exception;

public class InvalidProjectStatusException extends RuntimeException {
    public InvalidProjectStatusException(String message) {
        super(message);
    }
}
