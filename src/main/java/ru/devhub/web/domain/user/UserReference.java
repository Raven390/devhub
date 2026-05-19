package ru.devhub.web.domain.user;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object: минимальная ссылка на пользователя (только ID).
 * Используется там, где полная модель User не нужна (например, в ProjectMember).
 */
public record UserReference(UUID id) {
    public UserReference {
        Objects.requireNonNull(id, "UserReference.id must not be null");
    }
}
