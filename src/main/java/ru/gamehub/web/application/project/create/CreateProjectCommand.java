package ru.gamehub.web.application.project.create;

import ru.gamehub.web.application.common.Command;

import java.util.List;
import java.util.UUID;

/**
 * Команда на создание нового проекта.
 * <p>
 * Используется в application-слое (DDD, CQRS) как DTO между внешним API/контроллером и сервисом создания проекта.
 * Инкапсулирует все данные, необходимые для инициализации нового проекта.
 * </p>
 *
 * <b>Поля:</b>
 * <ul>
 *   <li><b>userId</b> — идентификатор пользователя, который будет владельцем проекта (не null).</li>
 *   <li><b>name</b> — название проекта (не null, не пустое, требования к длине и формату проверяются на уровне бизнес-логики).</li>
 *   <li><b>description</b> — описание проекта (может быть пустым).</li>
 * </ul>
 *
 * <b>Ограничения и валидация:</b>
 * <ul>
 *   <li>Валидация userId и корректности имени/описания происходит в сервисе или домене.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * CreateProjectCommand cmd = new CreateProjectCommand(userId, "Мой проект", "Описание");
 * Project project = createProjectService.handle(cmd);
 * </pre>
 *
 * @see ru.gamehub.web.application.project.create.CreateProjectService
 */
public record CreateProjectCommand(
        UUID ownerId,
        String name,
        String description,
        String shortDescription,
        UUID typeId,
        String status,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<UUID> memberIds
) implements Command {}