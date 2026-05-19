package ru.devhub.web.application.project.command.create;

import ru.devhub.web.application.common.Command;
import ru.devhub.web.domain.project.member.ProjectMemberStatus;

import java.util.List;
import java.util.UUID;

/**
 * Команда создания нового проекта.
 * <p>
 * Value Object — все поля финальные. Валидация бизнес-правил происходит
 * в {@link CreateProjectCommandHandler} и доменном слое.
 * </p>
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
        List<Member> members
) implements Command {

    /**
     * Участник команды создания. Не тащим web-DTO в application-слой.
     */
    public record Member(
            UUID userId,
            ProjectMemberStatus status,
            List<Integer> roleIds
    ) {}
}