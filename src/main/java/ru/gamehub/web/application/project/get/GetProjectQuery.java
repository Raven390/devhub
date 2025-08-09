package ru.gamehub.web.application.project.get;

import ru.gamehub.web.application.common.Command;

import java.util.UUID;

public record GetProjectQuery(UUID id) implements Command {
}
