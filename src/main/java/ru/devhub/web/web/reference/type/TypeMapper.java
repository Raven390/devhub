package ru.devhub.web.web.reference.type;

import org.mapstruct.Mapper;
import ru.devhub.web.domain.reference.project.type.ProjectType;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    TypeDto toDto(ProjectType type);
}
