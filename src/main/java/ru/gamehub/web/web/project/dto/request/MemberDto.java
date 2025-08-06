package ru.gamehub.web.web.project.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO для участника проекта в UpdateProjectRequest.
 */
public record MemberDto(
        UUID userId,
        Integer roleId,
        OffsetDateTime joinedAt // можно оставить nullable, если фронт не присылает
) {}

