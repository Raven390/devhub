package ru.devhub.web.web.user.dto.response;

import ru.devhub.web.web.reference.technology.TechnologyDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Полный профиль пользователя — ответ на {@code GET /users/me} и {@code GET /users/{userId}}.
 */
public record UserProfileResponse(
        UUID id,
        String name,
        String email,
        String headline,
        String bio,
        String avatarUrl,
        String githubUrl,
        List<TechnologyDto> skills,
        OffsetDateTime createdAt
) {}
