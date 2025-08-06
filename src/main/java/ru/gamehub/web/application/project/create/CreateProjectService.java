package ru.gamehub.web.application.project.create;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.application.project.ProjectAggregateAssembler;
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
    private final ProjectRepository projectRepository;
    private final ProjectAggregateAssembler projectAssembler;

    /**
     * Создает экземпляр сервиса создания проекта.
     *
     * @param projectRepository Репозиторий, в который сохраняются проекты
     */
    public CreateProjectService(ProjectRepository projectRepository, ProjectAggregateAssembler projectAssembler) {
        this.projectRepository = projectRepository;
        this.projectAssembler = projectAssembler;
    }


    /**
     * Обрабатывает команду создания проекта.
     *
     * @param command Команда с параметрами нового проекта
     * @return Созданный доменный объект проекта
     */
    @Override
    @Transactional
    public Project handle(CreateProjectCommand command) {
        Project project = projectAssembler.assemble(command);
        return projectRepository.save(project);
    }
}
