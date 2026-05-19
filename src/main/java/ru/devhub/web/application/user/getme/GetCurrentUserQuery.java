package ru.devhub.web.application.user.getme;

import java.util.UUID;

/**
 * Запрос профиля текущего аутентифицированного пользователя.
 *
 * @param userId UUID из JWT-клейма {@code business_id}
 */
public record GetCurrentUserQuery(UUID userId) {}
