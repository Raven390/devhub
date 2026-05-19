package ru.devhub.web.application.project.query.list;

import ru.devhub.web.application.common.Query;

import ru.devhub.web.domain.project.model.ProjectStatus;
import java.util.List;
import java.util.UUID;

/**
 * Запрос на получение страницы проектов с пагинацией и фильтрацией.
 * <p>
 * Переименован из {@code ListProjectsCommand} — операция является чтением,
 * поэтому относится к Query-стороне CQRS.
 * </p>
 *
 * @param page номер страницы (0-based, >= 0)
 * @param size количество элементов на странице (>= 1)
 */
public record ListProjectsQuery(
        int page,
        int size,
        ProjectStatus status,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        String search,
        UUID ownerId,
        UUID requestingUserId
) implements Query {

    public ListProjectsQuery {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");
    }
}