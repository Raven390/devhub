package ru.gamehub.web.web.project.dto.request;

/**
 * DTO для запроса списка проектов с пагинацией.
 */
public record ListProjectRequest(Integer page, Integer size) {}

