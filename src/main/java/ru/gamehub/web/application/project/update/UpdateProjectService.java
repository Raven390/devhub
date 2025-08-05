package ru.gamehub.web.application.project.update;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
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

    public UpdateProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    /**
     * Обрабатывает команду обновления проекта.
     * <p>
     * 1. Проверяет наличие проекта.<br>
     * 2. Проверяет права пользователя.<br>
     * 3. Обновляет и сохраняет проект.
     * </p>
     *
     * @param command команда с новыми параметрами проекта и id инициатора.
     * @return обновлённый {@link Project}
     * @throws ru.gamehub.web.domain.project.exception.ProjectNotFoundException если проект не найден.
     * @throws ru.gamehub.web.domain.project.exception.ProjectAccessDeniedException если инициатор не владелец.
     */
    @Override
    public Project handle(UpdateProjectCommand command) {
        Project project = projectRepository.findById(command.projectId())
                .orElseThrow(() -> new ProjectNotFoundException(command.projectId()));
        // Проверка, что обновляет владелец
        UUID ownerId = project.getOwner().getId();
        if (!ownerId.equals(command.ownerId())) {
            throw new ProjectAccessDeniedException(ownerId, command.ownerId());
        }

        // TODO
/*        projectRepository.save(updated);
        return updated;*/
        return project;
    }
}

