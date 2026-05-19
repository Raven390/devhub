package ru.devhub.web.domain.reference.project.type;

import java.util.Optional;
import java.util.UUID;

public interface ProjectTypeRepository {

    Optional<ProjectType> findById(UUID id);
    ProjectType save(ProjectType projectType);
    ProjectTypePage findPage();
}
