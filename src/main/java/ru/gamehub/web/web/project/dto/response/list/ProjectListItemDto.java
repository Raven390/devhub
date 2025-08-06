package ru.gamehub.web.web.project.dto.response.list;

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
        UUID ownerId,
        String ownerName,
        String typeName,
        String status,
        List<String> technologyNames,
        int membersCount,
        OffsetDateTime createdAt
) {}

