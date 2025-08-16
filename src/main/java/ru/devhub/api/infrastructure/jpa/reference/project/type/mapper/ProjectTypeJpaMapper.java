package ru.devhub.api.infrastructure.jpa.reference.project.type.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.devhub.api.domain.reference.project.type.ProjectType;
import ru.devhub.api.infrastructure.jpa.reference.project.type.ProjectTypeJpaEntity;

@Mapper(componentModel = "spring")
public interface ProjectTypeJpaMapper {
    ProjectType toDomain(ProjectTypeJpaEntity entity);
    ProjectTypeJpaEntity toEntity(ProjectType domain);

    @ObjectFactory
    default ProjectType createProjectType(ProjectTypeJpaEntity entity) {
        return ProjectType.create(entity.getId(), entity.getName());
    }
}
