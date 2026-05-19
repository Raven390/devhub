package ru.devhub.web.application.project.command.removemember;

import ru.devhub.web.application.common.Command;

import java.util.UUID;

public record RemoveMemberCommand(
        UUID projectId,
        UUID memberId,
        UUID requestingUserId
) implements Command {
}
