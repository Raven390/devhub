package ru.gamehub.web.application.reference.project.type.list;

import org.springframework.stereotype.Service;
import ru.gamehub.web.application.common.CommandHandler;
import ru.gamehub.web.domain.reference.project.type.ProjectTypePage;
import ru.gamehub.web.domain.reference.project.type.ProjectTypeRepository;

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