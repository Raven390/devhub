package ru.devhub.web.web.project.dto.member;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

/**
 * DTO участника проекта для входящих запросов (Create / Update).
 * <p>
 * Содержит только write-поля: идентификатор пользователя, желаемые роли и статус.
 * Read-поля (user object, joinedAt, leftAt) — только в {@link MemberResponseDto}.
 * </p>
 */
public record MemberRequestDto(
        @NotNull UUID userId,
        List<Integer> roleIds,
        @NotNull String status
) {}
