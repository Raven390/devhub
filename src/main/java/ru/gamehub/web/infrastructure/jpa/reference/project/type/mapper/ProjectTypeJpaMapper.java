package ru.gamehub.web.infrastructure.jpa.reference.project.type.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import ru.gamehub.web.domain.reference.project.type.ProjectType;
import ru.gamehub.web.infrastructure.jpa.reference.project.type.ProjectTypeJpaEntity;

@Mapper(componentModel = "spring")
public interface ProjectTypeJpaMapper {
    ProjectType toDomain(ProjectTypeJpaEntity entity);
    ProjectTypeJpaEntity toEntity(ProjectType domain);

    @ObjectFactory
    default ProjectType createProjectType(ProjectTypeJpaEntity entity) {
        return ProjectType.create(entity.getId(), entity.getName());
    }
}
