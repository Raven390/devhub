package ru.gamehub.web.application.project.create;

import ru.gamehub.web.application.common.Command;
import ru.gamehub.web.domain.user.User;

/**
 * Команда на создание нового проекта.
 * Используется в приложении как DTO между контроллером и доменной логикой.
 *
 * @param owner Владелец проекта
 * @param name Название проекта
 * @param description Описание проекта
 */
public record CreateProjectCommand(User owner, String name, String description) implements Command {}
