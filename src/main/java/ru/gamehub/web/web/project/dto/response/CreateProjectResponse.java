package ru.gamehub.web.web.project.dto.response;

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
        OwnerDto owner,
        List<TechnologyDto> technologies,
        List<RoleDto> roles,
        List<MemberDto> members,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {

    /**
     * Вложенный DTO для типа проекта.
     */
    public record TypeDto(UUID id, String name) {}

    /**
     * Вложенный DTO для владельца проекта.
     */
    public record OwnerDto(UUID id, String name) {}

    /**
     * Вложенный DTO для технологии.
     */
    public record TechnologyDto(Integer id, String name) {}

    /**
     * Вложенный DTO для роли.
     */
    public record RoleDto(Integer id, String name) {}

    /**
     * Вложенный DTO для участника.
     */
    public record MemberDto(UUID id, String name) {}
}
