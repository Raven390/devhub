package ru.gamehub.web.web.project.member;

import ru.gamehub.web.web.reference.role.RoleDto;
import ru.gamehub.web.web.user.dto.UserDto;

import java.time.OffsetDateTime;

/**
 * DTO для участника проекта в UpdateProjectRequest.
 */
public record MemberDto(
        UserDto user,
        RoleDto role,
        OffsetDateTime joinedAt // можно оставить nullable, если фронт не присылает
) {}

