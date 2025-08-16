package ru.devhub.api.application.reference.project.type.list;

import org.springframework.stereotype.Service;
import ru.devhub.api.application.common.CommandHandler;
import ru.devhub.api.domain.reference.project.type.ProjectTypePage;
import ru.devhub.api.domain.reference.project.type.ProjectTypeRepository;

@Service
public class ListTypeService implements CommandHandler<ListTypeCommand, ProjectTypePage> {
    private final ProjectTypeRepository projectTypeRepository;

    public ListTypeService(ProjectTypeRepository projectTypeRepository) {
        this.projectTypeRepository = projectTypeRepository;
    }


    @Override
    public ProjectTypePage handle(ListTypeCommand command) {
        return projectTypeRepository.findPage();
    }
}