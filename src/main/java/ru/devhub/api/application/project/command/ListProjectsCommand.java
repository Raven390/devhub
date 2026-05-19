package ru.devhub.api.application.project.command;

import ru.devhub.api.application.common.Command;

/**
 * Команда для получения списка проектов с пагинацией.
 * <p>
 * Используется в application-слое (DDD, CQRS) для передачи параметров запроса на получение страницы проектов.
 * Гарантирует корректность параметров пагинации (page >= 0, size >= 1) на этапе создания объекта.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>page</b> — номер страницы, начиная с 0.</li>
 *   <li><b>size</b> — количество проектов на странице.</li>
 * </ul>
 *
 * <b>Ограничения:</b>
 * <ul>
 *   <li>page не может быть меньше 0.</li>
 *   <li>size должен быть минимум 1.</li>
 *   <li>В случае нарушения — выбрасывается {@link IllegalArgumentException}.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * ListProjectsCommand cmd = new ListProjectsCommand(0, 20); // первая страница по 20 проектов
 * </pre>
 *
 * @see ListProjectsService
 */
public record ListProjectsCommand(int page, int size) implements Command {
    public ListProjectsCommand {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size < 1) throw new IllegalArgumentException("Size must be >= 1");
    }
}

