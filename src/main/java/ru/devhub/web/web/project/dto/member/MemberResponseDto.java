package ru.devhub.web.web.project.dto.member;

import ru.devhub.web.web.reference.role.RoleDto;
import ru.devhub.web.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO участника проекта для исходящих ответов (GET, POST, PUT).
 * <p>
 * Содержит только read-поля: полный объект пользователя, роли с деталями,
 * временны́е метки. Write-поля (userId, roleIds) — только в {@link MemberRequestDto}.
 * </p>
 */
public record MemberResponseDto(
        UUID id,
        UUID projectId,
        UserDto user,
        List<RoleDto> roles,
        String status,
        OffsetDateTime joinedAt,
        OffsetDateTime leftAt
) {}
