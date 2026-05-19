package ru.devhub.web.web.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.devhub.web.domain.project.exception.InvalidProjectStatusException;
import ru.devhub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.reference.project.role.exception.RoleNotFoundException;
import ru.devhub.web.domain.reference.project.technology.exception.TechnologyNotFoundException;
import ru.devhub.web.domain.reference.project.type.exception.ProjectTypeNotFoundException;
import ru.devhub.web.domain.user.exception.UserAlreadyExistsException;
import ru.devhub.web.domain.user.exception.UserNotFoundException;
import ru.devhub.web.domain.user.exception.UserRegistrationException;
import ru.devhub.web.web.dto.ErrorResponse;

import java.time.Instant;
import java.util.stream.Collectors;

/**
 * Глобальный обработчик ошибок.
 * Перехватывает все кастомные доменные исключения и преобразует их в стандартный
 * {@link ErrorResponse} с правильным HTTP-статусом согласно контракту openapi.yaml.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    public static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // =====================================================================
    // ВАЛИДАЦИЯ (400 Bad Request)
    // =====================================================================

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException ex, HttpServletRequest req) {
        String details = ex.getConstraintViolations().stream()
                .map(GlobalExceptionHandler::formatViolation)
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST, details.isBlank() ? "Validation failed" : details, req);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(
            MethodArgumentNotValidException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST,
                details.isBlank() ? "Request body validation failed" : details, req);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        return error(HttpStatus.BAD_REQUEST,
                details.isBlank() ? "Binding validation failed" : details, req);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(
            MissingServletRequestParameterException ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "Missing request parameter: " + ex.getParameterName(), req);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, TypeMismatchException.class})
    public ResponseEntity<ErrorResponse> handleTypeMismatch(Exception ex, HttpServletRequest req) {
        return error(HttpStatus.BAD_REQUEST, "Parameter type mismatch: " + ex.getMessage(), req);
    }

    // =====================================================================
    // PROJECT-ДОМЕН (404, 403, 422)
    // =====================================================================

    /** 404 — проект не найден */
    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFound(
            ProjectNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /** 403 — только владелец может изменять проект */
    @ExceptionHandler(ProjectAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleProjectAccessDenied(
            ProjectAccessDeniedException ex, HttpServletRequest req) {
        return error(HttpStatus.FORBIDDEN, ex.getMessage(), req);
    }

    /** 422 — невалидный статус проекта */
    @ExceptionHandler(InvalidProjectStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidProjectStatus(
            InvalidProjectStatusException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    /** 422 — несуществующая технология */
    @ExceptionHandler(TechnologyNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTechnologyNotFound(
            TechnologyNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    /** 422 — несуществующая роль */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(
            RoleNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    /** 422 — несуществующий тип проекта */
    @ExceptionHandler(ProjectTypeNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectTypeNotFound(
            ProjectTypeNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), req);
    }

    // =====================================================================
    // USER-ДОМЕН (404, 409, 500)
    // =====================================================================

    /** 404 — пользователь не найден */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, HttpServletRequest req) {
        return error(HttpStatus.NOT_FOUND, ex.getMessage(), req);
    }

    /** 409 — пользователь уже существует */
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(
            UserAlreadyExistsException ex, HttpServletRequest req) {
        return error(HttpStatus.CONFLICT, ex.getMessage(), req);
    }

    /** 500 — ошибка регистрации пользователя */
    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistration(
            UserRegistrationException ex, HttpServletRequest req) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }

    // =====================================================================
    // FALLBACK (500)
    // =====================================================================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest req) {
        LOG.error("Unexpected error on {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), req);
    }

    // =====================================================================
    // helpers
    // =====================================================================

    private ResponseEntity<ErrorResponse> error(HttpStatus status, String message, HttpServletRequest req) {
        return ResponseEntity.status(status).body(new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                req.getRequestURI(),
                Instant.now()
        ));
    }

    private static String formatViolation(ConstraintViolation<?> v) {
        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        field = field.isBlank() ? "<param>" : field;
        return field + ": " + v.getMessage();
    }

    private String formatFieldError(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        Object rejected = fe.getRejectedValue();
        return rejected != null
                ? field + ": " + msg + " (rejected=" + rejected + ")"
                : field + ": " + msg;
    }
}
