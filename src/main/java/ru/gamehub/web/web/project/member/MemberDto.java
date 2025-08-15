package ru.gamehub.web.web.project.member;

import ru.gamehub.web.web.reference.role.RoleDto;
import ru.gamehub.web.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для участника проекта в UpdateProjectRequest.
 */
public record MemberDto(
        UUID id,
        UUID projectId,
        UserDto user,
        List<RoleDto> roles,
        String status,
        OffsetDateTime joinedAt,
        OffsetDateTime leftAt
) {}

