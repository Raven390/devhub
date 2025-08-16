package ru.devhub.api.web.reference.type;

import org.mapstruct.Mapper;
import ru.devhub.api.domain.reference.project.type.ProjectType;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    TypeDto toDto(ProjectType type);
}
