package ru.gamehub.web.web.project.dto.response;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Ответ на операцию создания проекта.
 * Используется как DTO для возврата информации о новом проекте через API.
 *
 * @param id          Уникальный идентификатор проекта.
 * @param ownerId     Идентификатор владельца проекта.
 * @param name        Название проекта.
 * @param description Краткое описание проекта.
 * @param createdAt   Дата и время создания проекта.
 * @param updatedAt   Дата и время последнего обновления проекта.
 */
public record CreateProjectResponse(UUID id, UUID ownerId, String name, String description,
                                    OffsetDateTime createdAt, OffsetDateTime updatedAt) {}
