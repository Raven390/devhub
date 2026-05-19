package ru.devhub.web.application.project.query.list;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.QueryHandler;
import ru.devhub.web.domain.project.model.ProjectPage;
import ru.devhub.web.domain.project.repository.ProjectRepository;

/**
 * Обработчик запроса {@link ListProjectsQuery}.
 * Транзакция — readOnly (только чтение).
 */
@Service
public class ListProjectsQueryHandler implements QueryHandler<ListProjectsQuery, ProjectPage> {

    private final ProjectRepository projectRepository;

    public ListProjectsQueryHandler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectPage handle(ListProjectsQuery query) {
        return projectRepository.findPage(query.page(), query.size());
    }
}