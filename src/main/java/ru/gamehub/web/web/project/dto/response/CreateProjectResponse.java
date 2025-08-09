package ru.gamehub.web.web.project.dto.response;

import ru.gamehub.web.web.project.member.MemberDto;
import ru.gamehub.web.web.reference.role.RoleDto;
import ru.gamehub.web.web.reference.technology.TechnologyDto;
import ru.gamehub.web.web.reference.type.TypeDto;
import ru.gamehub.web.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO ответа на создание проекта.
 * Позволяет фронту отрисовать карточку/деталку без дополнительных запросов.
 */
public record CreateProjectResponse(
        UUID id,
        String name,
        String description,
        String shortDescription,
        String status,
        TypeDto type,
        UserDto owner,
        List<TechnologyDto> technologies,
        List<RoleDto> roles,
        List<MemberDto> members,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
