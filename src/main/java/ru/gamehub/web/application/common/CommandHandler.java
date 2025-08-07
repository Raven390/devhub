package ru.gamehub.web.application.common;

/**
 * Унифицированный интерфейс для обработки команд приложения в стиле DDD/CQRS.
 * <p>
 * Реализуется каждым сервисом, отвечающим за выполнение конкретного действия (use case),
 * принимающим команду типа {@code C} и возвращающим результат типа {@code R}.
 *
 * @param <C> тип команды, расширяющий {@link Command}
 * @param <R> тип результата обработки команды
 */
public interface CommandHandler<C extends Command, R> {

    /**
     * Обрабатывает переданную команду.
     *
     * @param command команда, содержащая входные данные
     * @return результат выполнения команды
     */
    R handle(C command);
}

