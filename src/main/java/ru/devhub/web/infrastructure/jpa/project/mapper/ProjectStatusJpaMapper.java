package ru.devhub.web.infrastructure.jpa.project.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.ValueMapping;
import ru.devhub.web.domain.project.model.ProjectStatus;
import ru.devhub.web.infrastructure.jpa.project.model.ProjectStatusJpaEnum;

@Mapper(componentModel = "spring")
public interface ProjectStatusJpaMapper {
    @ValueMapping(source = "RECRUITING", target = "RECRUITING")
    @ValueMapping(source = "ACTIVE", target = "ACTIVE")
    @ValueMapping(source = "ARCHIVED", target = "ARCHIVED")
    @ValueMapping(source = "DRAFT", target = "DRAFT")
    ProjectStatus toDomain(ProjectStatusJpaEnum entity);

    @InheritInverseConfiguration
    ProjectStatusJpaEnum toEntity(ProjectStatus domain);
}
