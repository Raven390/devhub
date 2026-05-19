package ru.devhub.api.web.project.dto;

import ru.devhub.api.web.reference.role.RoleDto;
import ru.devhub.api.web.user.dto.UserDto;

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

