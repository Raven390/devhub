package ru.devhub.api.application.project.query;

import ru.devhub.api.application.common.Command;

import java.util.UUID;

public record GetProjectQuery(UUID id) implements Command {
}
