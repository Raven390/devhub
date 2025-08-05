package ru.gamehub.web.web.project.dto.response.list;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ProjectListItemDto(
        UUID id,
        String name,
        String description,
        UUID ownerId,
        OffsetDateTime createdAt
) {}

