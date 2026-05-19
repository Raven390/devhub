package ru.devhub.web.web.project.dto.response;

import ru.devhub.web.web.reference.role.RoleDto;
import ru.devhub.web.web.reference.technology.TechnologyDto;
import ru.devhub.web.web.reference.type.TypeDto;
import ru.devhub.web.web.user.dto.UserDto;

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

