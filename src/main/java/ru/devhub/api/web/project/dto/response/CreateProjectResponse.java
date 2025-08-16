package ru.devhub.api.web.project.dto.response;

import ru.devhub.api.web.project.member.MemberDto;
import ru.devhub.api.web.reference.role.RoleDto;
import ru.devhub.api.web.reference.technology.TechnologyDto;
import ru.devhub.api.web.reference.type.TypeDto;
import ru.devhub.api.web.user.dto.UserDto;

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
