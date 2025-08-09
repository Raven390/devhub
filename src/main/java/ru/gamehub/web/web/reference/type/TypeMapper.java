package ru.gamehub.web.web.reference.type;

import org.mapstruct.Mapper;
import ru.gamehub.web.domain.reference.project.type.ProjectType;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    TypeDto toDto(ProjectType type);
}
