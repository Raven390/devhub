package ru.devhub.web.infrastructure.jpa.reference.project.type.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.devhub.web.domain.reference.project.type.ProjectType;
import ru.devhub.web.infrastructure.jpa.reference.project.type.ProjectTypeJpaEntity;

@Mapper(componentModel = "spring")
public interface ProjectTypeJpaMapper {
    ProjectType toDomain(ProjectTypeJpaEntity entity);
    ProjectTypeJpaEntity toEntity(ProjectType domain);

    @ObjectFactory
    default ProjectType createProjectType(ProjectTypeJpaEntity entity) {
        return ProjectType.create(entity.getId(), entity.getName());
    }
}
