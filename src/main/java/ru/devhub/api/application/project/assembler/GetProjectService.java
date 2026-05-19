package ru.devhub.api.application.project.assembler;

import org.springframework.stereotype.Service;
import ru.devhub.api.application.common.CommandHandler;
import ru.devhub.api.application.project.query.GetProjectQuery;
import ru.devhub.api.domain.project.model.Project;
import ru.devhub.api.domain.project.repository.ProjectRepository;
import ru.devhub.api.domain.project.exception.ProjectNotFoundException;

@Service
public class GetProjectService implements CommandHandler<GetProjectQuery, Project> {
    private final ProjectRepository projectRepository;

    public GetProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }


    @Override
    public Project handle(GetProjectQuery command) {
        return projectRepository.findById(command.id()).orElseThrow(() -> new ProjectNotFoundException(command.id()));
    }
}
