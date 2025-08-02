package ru.gamehub.web.application.project.create;

import org.springframework.stereotype.Service;
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
@Service
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
     * @param createProjectCommand Команда с параметрами нового проекта
     * @return Созданный доменный объект проекта
     */
    @Override
    public Project handle(CreateProjectCommand createProjectCommand) {
        Project project = Project.create(
                createProjectCommand.owner(),
                createProjectCommand.name(),
                createProjectCommand.description()
        );
        return repository.save(project);
    }
}
