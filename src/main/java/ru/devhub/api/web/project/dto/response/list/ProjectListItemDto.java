package ru.devhub.api.web.project.dto.response.list;

import ru.devhub.api.web.reference.role.RoleDto;
import ru.devhub.api.web.reference.technology.TechnologyDto;
import ru.devhub.api.web.reference.type.TypeDto;
import ru.devhub.api.web.user.dto.UserDto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO для элемента списка проектов (карточка/лента).
 * Используется для быстрых списков, без лишних деталей.
 */
public record ProjectListItemDto(
        UUID id,
        String name,
        String shortDescription,
        UserDto owner,
        TypeDto typeName,
        String status,
        List<TechnologyDto> technologyNames,
        List<RoleDto> roleNames,
        List<UserDto> members,
        OffsetDateTime createdAt
) {}

