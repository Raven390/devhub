package ru.devhub.web.application.user.update;

import java.util.List;
import java.util.UUID;

/**
 * Команда обновления профиля текущего пользователя.
 * Все профильные поля nullable — передавай только то, что нужно изменить.
 */
public record UpdateUserProfileCommand(
        UUID userId,
        String name,
        String headline,
        String bio,
        String githubUrl,
        List<Integer> skillIds
) {}
