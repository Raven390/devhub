package ru.devhub.web.web.project.dto.request;


import ru.devhub.web.web.project.dto.member.MemberRequestDto;

import java.util.List;
import java.util.UUID;

/**
 * Запрос на создание нового проекта.
 * Используется как DTO для приёма данных через API.
 *
 * @param name        название проекта
 * @param description краткое описание проекта
 */

public record CreateProjectRequest(
        String name,
        String description,
        String shortDescription,
        UUID typeId,
        String status,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<MemberRequestDto> members // Может быть пустым, если участников нет на старте
) {}
