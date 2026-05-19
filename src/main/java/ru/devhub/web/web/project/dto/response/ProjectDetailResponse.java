package ru.devhub.web.web.project.dto.response;

import ru.devhub.web.web.project.dto.member.MemberResponseDto;
import ru.devhub.web.web.reference.role.RoleDto;
import ru.devhub.web.web.reference.technology.TechnologyDto;
import ru.devhub.web.web.reference.type.TypeDto;
import ru.devhub.web.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Полная карточка проекта.
 * <p>
 * Единый DTO для ответов на POST (201 Created), PUT (200 OK) и GET /{id} (200 OK).
 * Заменяет ранее раздельные {@code CreateProjectResponse} и {@code GetProjectResponse}.
 * </p>
 */
public record ProjectDetailResponse(
        UUID id,
        String name,
        String description,
        String shortDescription,
        String status,
        TypeDto type,
        UserDto owner,
        List<TechnologyDto> technologies,
        List<RoleDto> roles,
        List<MemberResponseDto> members,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
