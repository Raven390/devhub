package ru.devhub.api.domain.project.exception;

public class InvalidProjectStatusException extends RuntimeException {
    public InvalidProjectStatusException(String message) {
        super(message);
    }
}
