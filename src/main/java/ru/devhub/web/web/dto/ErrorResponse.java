package ru.devhub.web.web.dto;

import java.time.Instant;

/**
 * Универсальный ответ для ошибок API.
 */
public record ErrorResponse(
        int status,             // HTTP-код
        String error,           // Краткое название ошибки (например, "Conflict", "Bad Request")
        String message,         // Детальное сообщение (например, "User already exists")
        String path,            // URI, на который был запрос
        Instant timestamp       // Время ошибки
) {}