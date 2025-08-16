package ru.devhub.api.web.config;

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
import ru.devhub.api.domain.user.exception.UserAlreadyExistsException;
import ru.devhub.api.domain.user.exception.UserRegistrationException;
import ru.devhub.api.web.dto.ErrorResponse;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /* ====================  ВАЛИДАЦИЯ (400)  ==================== */

    /**
     * Валидация @RequestParam / @PathVariable / @RequestHeader при @Validated на контроллере.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest req) {
        // Собираем "поле: сообщение" для всех нарушений
        String details = ex.getConstraintViolations().stream()
                .map(GlobalExceptionHandler::formatViolation)
                .collect(Collectors.joining("; "));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                details.isBlank() ? "Validation failed" : details,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Валидация тела запроса (@Valid @RequestBody) — ошибки биндинга полей.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBodyValidation(MethodArgumentNotValidException ex,
                                                              HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                details.isBlank() ? "Request body validation failed" : details,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Валидация @ModelAttribute / простых биндингов без @RequestBody.
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest req) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                details.isBlank() ? "Binding validation failed" : details,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** Отсутствует обязательный параметр запроса. */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex,
                                                            HttpServletRequest req) {
        String msg = "Missing request parameter: " + ex.getParameterName();
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /** Неверный тип параметра (например, limit=abc). */
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class, TypeMismatchException.class })
    public ResponseEntity<ErrorResponse> handleTypeMismatch(Exception ex, HttpServletRequest req) {
        String msg = "Parameter type mismatch: " + ex.getMessage();
        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                msg,
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /* ====================  USER-ДОМЕН (409/500)  ==================== */

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserExists(UserAlreadyExistsException ex, HttpServletRequest req) {
        ErrorResponse resp = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(resp);
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationException(UserRegistrationException ex,
                                                                         HttpServletRequest req) {
        ErrorResponse resp = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    /* ====================  FALLBACK (500)  ==================== */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex, HttpServletRequest req) {
        LOG.error("Unexpected error on {}: {}", req.getRequestURI(), ex.getMessage(), ex);
        ErrorResponse resp = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                ex.getMessage(),
                req.getRequestURI(),
                Instant.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(resp);
    }

    /* ====================  helpers  ==================== */

    private static String formatViolation(ConstraintViolation<?> v) {
        // propertyPath приходит как что-то вроде "search.query" или "search.limit"
        String path = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
        field = (field.isBlank()) ? "<param>" : field;
        return field + ": " + v.getMessage();
    }

    private String formatFieldError(FieldError fe) {
        String field = fe.getField();
        String msg = fe.getDefaultMessage();
        Object rejected = fe.getRejectedValue();
        if (rejected != null) {
            return field + ": " + msg + " (rejected=" + rejected + ")";
        }
        return field + ": " + msg;
    }
}
