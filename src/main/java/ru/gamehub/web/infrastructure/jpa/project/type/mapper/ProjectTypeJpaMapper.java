package ru.gamehub.web.infrastructure.jpa.project.type.mapper;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.project.type.ProjectType;
import ru.gamehub.web.infrastructure.jpa.project.type.ProjectTypeJpaEntity;

@Mapper(componentModel = "spring")
public interface ProjectTypeJpaMapper {
    ProjectType toDomain(ProjectTypeJpaEntity entity);
    ProjectTypeJpaEntity toEntity(ProjectType domain);
}
