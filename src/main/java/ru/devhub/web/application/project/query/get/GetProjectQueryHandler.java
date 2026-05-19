package ru.devhub.web.application.project.query.get;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.devhub.web.application.common.QueryHandler;
import ru.devhub.web.domain.project.exception.ProjectNotFoundException;
import ru.devhub.web.domain.project.model.Project;
import ru.devhub.web.domain.project.repository.ProjectRepository;

/**
 * Обработчик запроса {@link GetProjectQuery}.
 * Транзакция — readOnly (только чтение).
 */
@Service
public class GetProjectQueryHandler implements QueryHandler<GetProjectQuery, Project> {

    private final ProjectRepository projectRepository;

    public GetProjectQueryHandler(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Project handle(GetProjectQuery query) {
        return projectRepository.findById(query.id())
                .orElseThrow(() -> new ProjectNotFoundException(query.id()));
    }
}
