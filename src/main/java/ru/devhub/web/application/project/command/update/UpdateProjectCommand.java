package ru.devhub.web.application.project.command.update;

import ru.devhub.web.application.common.Command;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Команда обновления проекта.
 * <p>
 * Только владелец (ownerId == project.owner.id) может инициировать обновление.
 * Проверка прав — в {@link UpdateProjectCommandHandler}.
 * </p>
 */
public record UpdateProjectCommand(
        UUID projectId,
        UUID ownerId,
        String name,
        String description,
        String shortDescription,
        String status,
        UUID typeId,
        List<Integer> technologyIds,
        List<Integer> roleIds,
        List<Member> members
) implements Command {

    public record Member(
            UUID userId,
            UUID projectId,
            ProjectMemberStatus status,
            List<Integer> roleId,
            OffsetDateTime joinedAt,
            OffsetDateTime leftAt
    ) {}
}
