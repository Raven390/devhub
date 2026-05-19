package ru.devhub.api.application.project.assembler;

import org.springframework.stereotype.Service;
import ru.devhub.api.application.common.CommandHandler;
import ru.devhub.api.application.project.command.ListProjectsCommand;
import ru.devhub.api.domain.project.model.ProjectPage;
import ru.devhub.api.domain.project.repository.ProjectRepository;

/**
 * Application-сервис для получения страницы проектов с пагинацией.
 * <p>
 * Реализует паттерн CommandHandler (DDD, CQRS): принимает {@link ListProjectsCommand} и возвращает {@link ProjectPage}.
 * Вынесение логики пагинации в application-слой позволяет централизованно валидировать и реализовывать бизнес-ограничения на уровне приложения.
 * </p>
 *
 * <p>
 * <b>Side effects:</b> Только чтение (read-model), не изменяет состояние системы.<br>
 * <b>Thread-safety:</b> Потокобезопасность определяется реализацией {@link ProjectRepository}.
 * </p>
 *
 * <b>Пример использования:</b>
 * <pre>
 * ProjectPage page = listProjectsService.handle(new ListProjectsCommand(0, 20));
 * </pre>
 *
 * @see ListProjectsCommand
 * @see ProjectPage
 */
@Service
public class ListProjectsService implements CommandHandler<ListProjectsCommand, ProjectPage> {
    private final ProjectRepository projectRepository;

    public ListProjectsService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    /**
     * Возвращает страницу проектов по параметрам пагинации.
     *
     * @param command параметры страницы и размера
     * @return {@link ProjectPage} — страница проектов с метаинформацией
     */
    @Override
    public ProjectPage handle(ListProjectsCommand command) {
        return projectRepository.findPage(command.page(), command.size());
    }
}
