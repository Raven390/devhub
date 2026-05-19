package ru.devhub.web.application.project.command.join;

import ru.devhub.web.application.common.Command;

import java.util.List;
import java.util.UUID;

public record JoinProjectCommand(
        UUID projectId,
        UUID userId,
        List<Integer> roleIds
) implements Command {
}
