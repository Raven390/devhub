package ru.gamehub.web.application.project.update;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException;
import ru.gamehub.web.domain.project.exception.ProjectNotFoundException;

import java.util.UUID;

/**
 * Application-сервис для обновления информации о проекте.
 * <p>
 * Реализует паттерн CommandHandler (DDD, CQRS), обрабатывая {@link UpdateProjectCommand}.
 * Гарантирует, что обновление производится только владельцем проекта.
 * </p>
 *
 * <b>Side effects:</b> изменяет состояние проекта и сохраняет изменения в {@link ProjectRepository}.
 * Потокобезопасность определяется реализацией репозитория.
 * <p>
 * <b>Исключения:</b>
 * <ul>
 *   <li>{@link ru.gamehub.web.domain.project.exception.ProjectNotFoundException} — если проект не найден.</li>
 *   <li>{@link ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException} — если обновление пытается выполнить не владелец.</li>
 * </ul>
 *
 * <b>Пример использования:</b>
 * <pre>
 * Project updated = updateProjectService.handle(cmd);
 * </pre>
 *
 * @see UpdateProjectCommand
 * @see ProjectRepository
 */
@Service
public class UpdateProjectService implements CommandHandler<UpdateProjectCommand, Project> {
    private final ProjectRepository projectRepository;
    private final ProjectAggregateAssembler assembler;

    public UpdateProjectService(ProjectRepository projectRepository, ProjectAggregateAssembler assembler) {
        this.projectRepository = projectRepository;
        this.assembler = assembler;
    }

    @Override
    public Project handle(UpdateProjectCommand command) {
        Project existing = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));

        UUID ownerId = existing.getOwner().getId();
        if (!ownerId.equals(command.ownerId())) {
            throw new ProjectAccessDeniedException(ownerId, command.ownerId());
        }

        Project updated = assembler.updateAggregate(existing, command);
        return projectRepository.save(updated);
    }
}


