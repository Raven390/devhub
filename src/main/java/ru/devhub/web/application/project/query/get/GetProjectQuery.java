package ru.devhub.web.application.project.query.get;

import ru.devhub.web.application.common.Query;

import java.util.UUID;

/**
 * Запрос на получение проекта по идентификатору.
 */
public record GetProjectQuery(UUID id) implements Query {}
