package ru.gamehub.web.web.project.dto.request;

/**
 * Запрос на создание нового проекта.
 * Используется как DTO для приёма данных через API.
 *
 * @param name        название проекта
 * @param description краткое описание проекта
 */
public record CreateProjectRequest(String name, String description) {}

