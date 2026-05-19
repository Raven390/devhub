package ru.devhub.web.web.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Тело запроса {@code PUT /users/me}.
 * Все поля опциональны — передавай только то, что нужно обновить.
 */
public record UpdateUserProfileRequest(

        @Size(min = 1, max = 100, message = "name must be between 1 and 100 characters")
        String name,

        @Size(max = 200, message = "headline must be at most 200 characters")
        String headline,

        @Size(max = 1000, message = "bio must be at most 1000 characters")
        String bio,

        @Pattern(
                regexp = "^(https?://.*)?$",
                message = "githubUrl must be a valid URL"
        )
        @Size(max = 500)
        String githubUrl,

        List<Integer> skillIds
) {}
