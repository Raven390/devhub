package ru.gamehub.web.web.project.dto.request;

/**
 * Запрос на создание нового проекта.
 * Используется как DTO для приёма данных через API.
 *
 * @param name        название проекта
 * @param description краткое описание проекта
 */
import java.util.List;
import java.util.UUID;

/**
 * DTO для создания нового проекта через REST API.
 * Используется в контроллере для получения данных из запроса от фронта.
 */
public record CreateProjectRequest(
        String name,
        String description,
        String shortDescription,
        UUID typeId,
        String status,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<UUID> members // Может быть пустым, если участников нет на старте
) {}
