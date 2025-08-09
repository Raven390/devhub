package ru.gamehub.web.application.project.get;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.domain.project.Project;
import ru.gamehub.web.domain.project.ProjectRepository;
import ru.gamehub.web.domain.project.exception.ProjectNotFoundException;

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
