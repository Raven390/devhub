package ru.gamehub.web.application.project.create;

import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;

/**
 * Сервис приложения, отвечающий за обработку команды {@link CreateProjectCommand}.
 * Создаёт новый проект и сохраняет его в {@link ru.gamehub.web.domain.project.ProjectRepository}.
 * <p>
 * Реализация {@link CommandHandler}, позволяющая использовать единый контракт
 * для всех командных операций приложения.
 */
public class CreateProjectService implements CommandHandler<CreateProjectCommand, Project> {
    private final ProjectRepository repository;

    /**
     * Создает экземпляр сервиса создания проекта.
     *
     * @param repository Репозиторий, в который сохраняются проекты
     */
    public CreateProjectService(ProjectRepository repository) {
        this.repository = repository;
    }

    /**
     * Обрабатывает команду создания проекта.
     *
     * @param cmd Команда с параметрами нового проекта
     * @return Созданный доменный объект проекта
     */
    @Override
    public Project handle(CreateProjectCommand cmd) {
        Project project = Project.create(cmd.owner(), cmd.name(), cmd.description());
        repository.save(project);
        return project;
    }
}
