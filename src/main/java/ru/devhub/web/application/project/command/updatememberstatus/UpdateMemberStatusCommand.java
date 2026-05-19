package ru.devhub.web.application.project.command.updatememberstatus;

import ru.devhub.web.application.common.Command;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;

import java.util.UUID;

public record UpdateMemberStatusCommand(
        UUID projectId,
        UUID memberId,
        ProjectMemberStatus newStatus,
        UUID requestingUserId
) implements Command {
}
